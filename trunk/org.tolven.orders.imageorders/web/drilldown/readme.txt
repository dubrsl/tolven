This folder will contain .xhtml files that are placeholder drilldown pages.
Most drilldown pages should display all contents of the document. There are two strategies:
1. Display only data from the placeholder, not the document itself.
2. Display the document, being careful to check the media type and namespace of the (xml) document.

To avoid conflicts with other pages that might be merged to this folder, consider using a sub-folder
using your plugin id for those pages uniquely yours.
A file in this directory with the same name as a file in the Tolven core will override the default. 