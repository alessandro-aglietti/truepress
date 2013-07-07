package us.truepress.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import jdo.PMF;

import org.apache.commons.lang3.exception.ExceptionUtils;

import queue.Queues;
import us.truepress.model.BoxEntity;
import us.truepress.model.PressEntity;
import us.truepress.model.RssEntity;
import us.truepress.model.RssEntryEntity;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.sun.jersey.api.view.Viewable;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

@Path("/press")
public class PressController {

	private static final Logger log = Logger.getLogger(PressController.class.getName());

	@Path("")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response post(@FormParam("pressname") String pressName) {
		log.info("pressName " + pressName);
		PersistenceManager pm = PMF.get().getPersistenceManager();
		PressEntity press = new PressEntity(pressName);
		try {
			pm.makePersistent(press);
		} finally {
			pm.close();
		}

		try {
			return Response.seeOther(new URI("/press/" + press.getId())).build();
		} catch (URISyntaxException e) {
			log.severe(ExceptionUtils.getStackTrace(e));
			return Response.serverError().entity(ExceptionUtils.getStackTrace(e)).build();
		}
	}

	@Path("/{id}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response get(@PathParam("id") Long pressId) {
		log.info("pressId " + pressId);

		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("boxurl", "/press/" + pressId + "/box");

		PersistenceManager pm = PMF.get().getPersistenceManager();
		// pm.getFetchPlan().setGroup(FetchPlan.ALL);
		pm.getFetchPlan().addGroup("boxs");
		pm.getFetchPlan().addGroup("rss");
		pm.getFetchPlan().addGroup("rssentries");

		try {
			PressEntity press = pm.getObjectById(PressEntity.class, pressId);

			List<BoxEntity> boxs = (List<BoxEntity>) pm.detachCopyAll(press.getBoxs());
			for (int i = 0; i < boxs.size(); i++) {
				if (boxs.get(i).getRss() != null) {
					boxs.get(i).setRss((List<RssEntity>) pm.detachCopyAll(boxs.get(i).getRss()));
				}
			}

			model.put("boxs", boxs);
		} finally {
			pm.close();
		}

		return Response.ok(new Viewable("/press", model)).build();
	}

	@Path("/{id}/box")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postBox(@PathParam("id") Long pressId, BoxEntity box) {
		log.info("");

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			PressEntity press = pm.getObjectById(PressEntity.class, pressId);
			press.getBoxs().add(box);
		} finally {
			pm.close();
		}

		return Response.ok(box).build();
	}

	@Path("/{id}/box/{boxId}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateBox(@PathParam("id") Long pressId, @PathParam("boxId") Long boxId, BoxEntity box) {
		log.info("");

		PersistenceManager pm = PMF.get().getPersistenceManager();
		BoxEntity boxToUpdate;

		try {
			boxToUpdate = pm.getObjectById(BoxEntity.class,
					KeyFactory.createKey(KeyFactory.createKey(PressEntity.class.getSimpleName(), pressId), BoxEntity.class.getSimpleName(), boxId));
			boxToUpdate.setHeight(box.getHeight());
			boxToUpdate.setLeft(box.getLeft());
			boxToUpdate.setTop(box.getTop());
			boxToUpdate.setWidth(box.getWidth());
			pm.detachCopy(boxToUpdate);
		} finally {
			pm.close();
		}

		return Response.ok(boxToUpdate).build();
	}

	@Path("/{id}/box/{boxId}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response viewBox(@PathParam("id") Long pressId, @PathParam("boxId") Long boxId) {
		log.info("");

		PersistenceManager pm = PMF.get().getPersistenceManager();

		HashMap<String, Object> model = new HashMap<String, Object>();

		model.put("boxurl", "/press/" + pressId + "/box/" + boxId + "/rss");

		try {
			BoxEntity box = pm.getObjectById(BoxEntity.class,
					KeyFactory.createKey(KeyFactory.createKey(PressEntity.class.getSimpleName(), pressId), BoxEntity.class.getSimpleName(), boxId));
			BoxEntity detached = pm.detachCopy(box);
			if (box.getRss() != null) {
				detached.setRss((List<RssEntity>) pm.detachCopyAll(box.getRss()));
			}
			model.put("box", detached);
		} finally {
			pm.close();
		}

		return Response.ok(new Viewable("/box", model)).build();
	}

	@Path("/{id}/box/{boxId}/rss")
	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response addRss(@PathParam("id") Long pressId, @PathParam("boxId") Long boxId, @FormParam("rssurl") String rssurl) {
		log.info(rssurl);

		PersistenceManager pm = PMF.get().getPersistenceManager();

		RssEntity rss = new RssEntity(rssurl);
		try {
			BoxEntity box = pm.getObjectById(BoxEntity.class,
					KeyFactory.createKey(KeyFactory.createKey(PressEntity.class.getSimpleName(), pressId), BoxEntity.class.getSimpleName(), boxId));

			pm.makePersistent(rss);

			box.getRss().add(rss);

			// task to fetch
			Queues.DEFAULT.add(TaskOptions.Builder.withCountdownMillis(0).method(Method.POST)
					.url("/press/" + pressId + "/box/" + boxId + "/rss/" + rss.getId()));

		} finally {
			pm.close();
		}

		try {
			return Response.seeOther(new URI("/press/" + pressId + "/box/" + boxId)).build();
		} catch (URISyntaxException e) {
			log.severe(ExceptionUtils.getStackTrace(e));
			return Response.serverError().entity(ExceptionUtils.getStackTrace(e)).build();
		}

	}

	@Path("/{id}/box/{boxId}/rss/{rssId}")
	@POST
	public Response fetchRss(@PathParam("id") Long pressId, @PathParam("boxId") Long boxId, @PathParam("rssId") Long rssId) {
		log.info("");

		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			RssEntity rss = pm.getObjectById(RssEntity.class, rssId);

			List<RssEntryEntity> entries = new ArrayList<RssEntryEntity>();

			URL url;
			try {
				url = new URL(rss.getUrl());
			} catch (MalformedURLException e) {
				log.severe(ExceptionUtils.getStackTrace(e));
				return Response.serverError().entity(ExceptionUtils.getStackTrace(e)).build();
			}
			XmlReader reader = null;

			URLConnection conn;
			try {
				conn = url.openConnection();
			} catch (IOException e) {
				log.severe(ExceptionUtils.getStackTrace(e));
				return Response.serverError().entity(ExceptionUtils.getStackTrace(e)).build();
			}

			conn.setReadTimeout(60000);

			try {

				reader = new XmlReader(conn);

				SyndFeed feed = new SyndFeedInput().build(reader);

				Iterator<SyndEntry> i = feed.getEntries().iterator();

				// FIXME messo un fermo a 4 rss entries
				int count = 0;
				while (i.hasNext() && count < 3) {
					SyndEntry next = i.next();
					RssEntryEntity entry = new RssEntryEntity(next.getUri());
					SyndContent desc = next.getDescription();
					if (desc != null) {
						String content = desc.getValue();
						if (content.length() > 400) {
							content = content.substring(0, 400);
						}
						entry.setContent(content);
					} else if (!next.getContents().isEmpty()) {
						String content = ((SyndContent) next.getContents().get(0)).getValue();
						if (content.length() > 400) {
							content = content.substring(0, 400);
						}
						entry.setContent(content);
					}

					entry.setTitle(next.getTitle());
					entry.setUpdated(next.getPublishedDate());

					entries.add(entry);

					count++;
				}
			} catch (IOException e) {
				log.severe(ExceptionUtils.getStackTrace(e));
				return Response.serverError().entity(ExceptionUtils.getStackTrace(e)).build();
			} catch (IllegalArgumentException e) {
				log.severe(ExceptionUtils.getStackTrace(e));
				return Response.serverError().entity(ExceptionUtils.getStackTrace(e)).build();
			} catch (FeedException e) {
				log.severe(ExceptionUtils.getStackTrace(e));
				return Response.serverError().entity(ExceptionUtils.getStackTrace(e)).build();
			} finally {
				// Chiudiamo lo stream precedentemente aperto.
				if (reader != null)
					try {
						reader.close();
					} catch (IOException e) {
						log.severe(ExceptionUtils.getStackTrace(e));
						return Response.serverError().entity(ExceptionUtils.getStackTrace(e)).build();
					}
			}

			rss.getEntries().addAll(entries);
		} finally {
			pm.close();
		}

		return Response.ok().build();
	}

	@Path("/{id}/box/{boxId}/del")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response deleteBox(@PathParam("id") Long pressId, @PathParam("boxId") Long boxId) {
		log.info("");

		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			BoxEntity box = pm.getObjectById(BoxEntity.class,
					KeyFactory.createKey(KeyFactory.createKey(PressEntity.class.getSimpleName(), pressId), BoxEntity.class.getSimpleName(), boxId));

			PressEntity press = pm.getObjectById(PressEntity.class, pressId);

			press.getBoxs().remove(box);

			pm.deletePersistent(box);
		} finally {
			pm.close();
		}

		try {
			return Response.seeOther(new URI("/press/" + pressId)).build();
		} catch (URISyntaxException e) {
			log.severe(ExceptionUtils.getStackTrace(e));
			return Response.serverError().entity(ExceptionUtils.getStackTrace(e)).build();
		}

	}
}