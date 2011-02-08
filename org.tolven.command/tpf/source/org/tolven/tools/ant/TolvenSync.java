package org.tolven.tools.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Sync;
import org.apache.tools.ant.types.FileSet;

public class TolvenSync {

    private static Logger logger = Logger.getLogger(TolvenSync.class);

    public static void sync(List<File> plugins, File destDir) {
        sync(plugins, destDir, null);
    }

    public static void sync(List<File> plugins, File destDir, String preserveIncludes) {
        Project project = new Project();
        project.init();
        Sync sync = new Sync();
        sync.setProject(project);
        sync.init();
        Map<File, List<File>> pluginParentDirMap = new HashMap<File, List<File>>();
        for (File plugin : plugins) {
            List<File> pluginList = pluginParentDirMap.get(plugin.getParentFile());
            if (pluginList == null) {
                pluginList = new ArrayList<File>();
                pluginParentDirMap.put(plugin.getParentFile(), pluginList);
            }
            pluginList.add(plugin);
        }
        for (File dir : pluginParentDirMap.keySet()) {
            StringBuffer buff = new StringBuffer();
            FileSet fileSet = new FileSet();
            fileSet.setDir(dir);
            for (File file : pluginParentDirMap.get(dir)) {
                buff.append("**/" + file.getName() + "/** ");
            }
            fileSet.setIncludes(buff.toString());
            logger.debug("sync for " + dir + " includes: " + buff.toString());
            sync.add(fileSet);
        }
        Sync.SyncTarget preserve = new Sync.SyncTarget();
        if (preserveIncludes != null) {
            preserve.setIncludes(preserveIncludes);
            sync.addPreserveInTarget(preserve);
        }
        sync.setTodir(destDir);
        if (preserveIncludes != null) {
            logger.debug("sync preserve includes: " + preserveIncludes);
        }
        logger.debug("syncing to: " + destDir.getPath());
        sync.execute();
        logger.debug("sync complete");
    }

    public static void sync(File srcDir, File destDir) {
        Project project = new Project();
        project.init();
        Sync sync = new Sync();
        sync.setProject(project);
        sync.init();
        FileSet fileSet = new FileSet();
        fileSet.setDir(srcDir);
        sync.add(fileSet);
        sync.setTodir(destDir);
        logger.debug("syncing: " + srcDir.getPath() + " to: " + destDir.getPath());
        sync.execute();
        logger.debug("sync complete");
    }

}
