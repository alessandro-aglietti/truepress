package queue;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;

public class Queues {
	public final static Queue DEFAULT = QueueFactory.getQueue("default");
}