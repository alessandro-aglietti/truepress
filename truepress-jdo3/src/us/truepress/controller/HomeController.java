package us.truepress.controller;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import jdo.PMF;
import us.truepress.model.PressEntity;

import com.sun.jersey.api.view.Viewable;

@Path("/")
public class HomeController {

	private static final Logger log = Logger.getLogger(HomeController.class.getName());

	public HomeController() {
		// TODO Auto-generated constructor stub
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response home() {
		log.info("");
		PersistenceManager pm = PMF.get().getPersistenceManager();
		HashMap<String, Object> model = new HashMap<String, Object>();
		try {
			List<PressEntity> press = (List<PressEntity>) pm.detachCopyAll((List<PressEntity>) pm.newQuery(PressEntity.class).execute());
			model.put("press", press);
		} finally {
			pm.close();
		}

		return Response.ok(new Viewable("/home", model)).build();
	}
}
