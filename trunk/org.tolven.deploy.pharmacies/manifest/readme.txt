The plugin is used to download flat file from surescript and load into database.The user need to add the following app server properties.

Name											Description
--------------------------------------------	--------------------------------
surescripts.download.url						SS download URL provided to you.
eprescription.surescripts.download.directory	Directory to save the SS flat file.

TPF command to download and load pharmacies.
-----------------------------------------------
On Windows : tpf -plugin org.tolven.deploy.pharmacies			
On Linux   : ./tpf.sh -plugin org.tolven.deploy.pharmacies		