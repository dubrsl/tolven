import org.apache.log4j.Logger;
import org.tolven.plugin.TolvenCommandPlugin;

public class HelloWorld extends TolvenCommandPlugin {
    protected Logger logger = Logger.getLogger(getClass());

    @Override
    protected void doStart() throws Exception {
        logger.info("*** Start HelloWorld***");
    }

    @Override
    protected void doStop() throws Exception {
        logger.info("*** Stop HelloWorld***");
    }

	@Override
	public void execute(String[] args) throws Exception {
        logger.info("*** HELLO-WORLD from HelloWorld***");
	}

}
