scala-simple-clinic
===================

Example of Scala Swing usage with some HTTP and JSON operaions setting up simple teminal app
As of now there is only part of the project (i.e. terminal-type application). The other part is web app (written in php) which, for now, i don't want to make public.
If I find enough time and motivation in future I'd write something simplified and put it here. Maybe even I'll do i18n for this :)

Some basic info how it works and why I decided to put this (somehow ugly and not to good written project) here on GitHub:
1. That's one of my very first projects in this language and with this GUI library (I had almost none of knowledge about Swing for Java or Scala before), and I found that sometimes examples of usage of some controls is very bad docummented (i.e. poor official docu and no examples in the net)
2. so this is some example of how to dynamically generate program layout, make window re-draw with previously also dynamically generated controls (this type of "layout management" has obvious security hole in the MitM type attacks, especially working with HTTP protocol instead of HTTPS)
3. most of the data manipulation, including more complex validation, saving, filtering, etc. is (in theory at least) done by the web serwer and the PHP application; only user-side validation is checking if all of required fields were filled
4. Scala app is sending request to server either as HTTP HEAD (to check status of some action or sth) HTTP GET (to get some data from the server) or HTTP POST to send data to the server (for example during addition or edition of some records)
5. Server, on the other hand, is sending data mostly as JSON or (in case of HTTP HEAD, or response to HTTP POST) just HTTP Header with proper response code (i.e.: 200 if everything OK, 400 if validation/saving error occured or 500 with more serious problems such as CSRF token problem or server error)
