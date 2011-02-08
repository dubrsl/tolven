package org.tolven.web.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.tolven.app.MenuEventHandler;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.MSAction;
import org.tolven.locale.TolvenResourceBundle;

public class MiscUtils {
    
    public static String createActionButtons(List<MSAction> actions, String element, TolvenResourceBundle appBundle) {
        /*
         * Use a Writer which more closely matches the writer on a servlet response (which does not make it to this method yet)
         */
        Writer writer = null;
        try {
            writer = new StringWriter();
            for (MSAction action : actions) {
                if (action.getMenuEventHandlerFactory() == null) {
                    createActionButtons(action, element, appBundle, writer);
                } else {
                    createHandlerActionButtons(action, element, appBundle, writer);
                }
            }
        } catch (IOException ex) {
            //TODO: Needs to be declared in the signature at some point
            throw new RuntimeException(ex);
        }
        return writer.toString();
    }

    public static void createActionButtons(MSAction action, String element, TolvenResourceBundle appBundle, Writer writer) throws IOException {
        MenuPath menuPath = new MenuPath(action.getPath(), new MenuPath(element));
        String path = menuPath.getPathString();
        writer.write("<button id='showDropDown' onclick=\"javascript:showActionOptions('");
        writer.write(path);
        writer.write("_dropdown_loc','");
        writer.write(path);
        writer.write("_drpDwn')\">");
        writer.write(action.getLocaleText(appBundle));
        writer.write("</button>");
        writer.write("<element id='");
        writer.write(path);
        writer.write("_dropdown_loc' style='position:relative;'/>");
        writer.write("<div id='");
        writer.write(path);
        writer.write("_drpDwn' class='drpDwn' style='display:none;'>");
        writer.write("</div>");
    }

    public static void createHandlerActionButtons(MSAction action, String element, TolvenResourceBundle appBundle, Writer writer) {
        String menuEventHandlerFactoryClassname = action.getMenuEventHandlerFactory();
        Class<?> menuEventHandlerFactoryClass = null;
        try {
            menuEventHandlerFactoryClass = Class.forName(menuEventHandlerFactoryClassname);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            //Print the stack trace, dump the button and return adding no button
            return;
        }
        try {
            MenuEventHandler menuEventHandler = MenuEventHandler.createMenuEventHandler(menuEventHandlerFactoryClass, action);
            menuEventHandler.setElement(element);
            menuEventHandler.setResourceBundle(appBundle);
            menuEventHandler.setWriter(writer);
            menuEventHandler.initializeAction();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

}
