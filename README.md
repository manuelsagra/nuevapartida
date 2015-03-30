# Nueva Partida

This is a (currently unfinished) project to catalog all the games released on the following systems:

* 3DO
* Amiga CD32
* CD-i
* CDTV
* FM Towns Marty
* Jaguar CD
* Mega CD
* Neo Geo CD
* PC Engine CD
* PC-FX
* Pippin
* Playdia
* PlayStation
* Saturn

It has three parts:

* `nuevapartida.sql`: A dump of the MySQL DB.
* `util`: A Java GUI tool to update the data.
* `web`: Web frontend for searching and displaying the database.

## Java dependencies

These libraries are needed for the Java tool:

* [Commons Lang](https://commons.apache.org/proper/commons-lang/)
* [jaunt](http://jaunt-api.com/) (Maybe it's better to migrate to the open source [jsoup](http://jsoup.org/) library)
* [Java Image Scaling](https://github.com/martinheidegger/java-image-scaling)
* [Java MySQL Connector](https://dev.mysql.com/downloads/connector/j/5.1.html)
* [Log4j](http://logging.apache.org/log4j/1.2/)

If you use Eclipse, change the workspace text enconding to UTF-8.

## Web interface

Maybe you need to change some paramaters in `.htaccess` and `includes/config.php` to make it work. And you have to enable `mod_rewrite` in the web server configuration.

I've used these resources to build the web interface:

* [famfamfam Flag Icons](http://www.famfamfam.com/lab/icons/flags/)
* [Google Charts](https://developers.google.com/chart/)
* [jQuery](https://jquery.com/) & [jQuery UI](https://jqueryui.com/)
* [Lato Font](https://www.google.com/fonts/specimen/Lato)
* [PHP-Barcode](http://www.ashberg.de/php-barcode/) (Install [genbarcode](http://www.ashberg.de/php-barcode/download/) and configure `$genbarcode_loc` in `php_barcode.php`)
