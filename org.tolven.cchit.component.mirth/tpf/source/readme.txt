This directory is seeded with a HelloWorld application.

Once you've seen this code work, you will most likely want to remove it. If you are not intending to add any code that runs in tpf, that is, in the configuration environment, then it can be removed completely:

   1. Edit the manifest/tolven.plugin.xml file:
   2. Remove class="HelloWorld"
   3. Comment or remove the whole <runtime> section
   4. In the tpf section, delete the HelloWorld.java file. 

If you do need code that runs in the tpf configuration client, such as a loader, then you can just refactor HelloWorld to another name, adding a package name prefix as well. Also, be sure to change the full class name in the manifest/tolven.plugin.xml file. 