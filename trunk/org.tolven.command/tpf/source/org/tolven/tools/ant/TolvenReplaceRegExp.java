package org.tolven.tools.ant;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.ReplaceRegExp;

public class TolvenReplaceRegExp {

    private static Logger logger = Logger.getLogger(TolvenReplaceRegExp.class);

    public static void replaceregexp(File file, String match, String replace) {
        replaceregexp(file, match, replace, null, false);
    }

    public static void replaceregexp(File file, String match, String replace, String flags, boolean byline) {
        if(!file.exists()) {
            throw new RuntimeException(file.getPath() + " does not exist");
        }
        Project project = new Project();
        project.init();
        ReplaceRegExp replaceRegExp = new ReplaceRegExp();
        replaceRegExp.setProject(project);
        replaceRegExp.init();
        replaceRegExp.setFile(file);
        replaceRegExp.setMatch(match);
        replaceRegExp.setReplace(replace);
        if (flags != null) {
            replaceRegExp.setFlags(flags);
        }
        replaceRegExp.setByLine(byline);
        logger.debug("replaceRegExp for: " + file.getPath() + " match: " + match);
        replaceRegExp.execute();
        logger.debug("replaceRegExp complete");
    }

}
