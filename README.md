LeMATo - LexicoMetric Analysis TOol
===================================

# Introduction

This work is based on my Master-Thesis, which is online available at: http://othes.univie.ac.at/41123/.

# Software-Stack #

This project relies on the work of many open source projects including:

* Grails 3.0.3 (Apache License 2.0)
* Elasticsearch 1.7.3 (Apache License 2.0)
* elasticsearch-groovy (Apache License 2.0) (groovy client api)
* httpbuilder (Apache License 2.0) (initializing index templates and indexed scripting)
* S-Space (GNU General Public License 2)
* twitter Bootstrap (MIT license 2015)
* bootstrap-table (MIT license 2012-2014)
* bootstrap-tagsinput (MIT license 2013)
* typeahead.js (MIT license, 2013-2014)
* d3js.org (BSD 3)

# Installation instructions 

## Prerequisits 

* Java Development Kit 1.8.x or above [Download](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

## Installation of Grails 

### Installation with SDKMAN

* Unix based systems can also use [SDKMAN](http://sdkman.io/) instead of the manual installation.

### Manual Installation

* Download Grails 3.0.3 on [Grails download site](https://grails.org/download.html)
* Set the GRAILS_HOME environment variable to the location where you extracted the zip

	* On Unix/Linux based systems this is typically a matter of adding something like the following export GRAILS_HOME=/path/to/grails to your profile	
	* On Windows this is typically a matter of setting an environment variable under My Computer/Advanced/Environment Variables

* Then add the bin directory to your PATH variable:
  * On Unix/Linux based systems this can be done by adding export PATH="$PATH:$GRAILS_HOME/bin" to your profile
  * On Windows this is done by modifying the Path environment variable under My Computer/Advanced/Environment Variables

* If Grails is working correctly you should now be able to type `grails -version` in the terminal window and see output similar to this: Grails version: 3.0.3

## Installation of Elasticsearch 

* Download and extract [Elasticsearch 1.7.4](https://www.elastic.co/downloads/past-releases/elasticsearch-1-7-4)
* Override the elasticsearch configuration under `config/elasticsearch.yml` with the content of  [this file](https://github.com/perdacherMartin/LeMATo/blob/master/elasticsearch/elasticsearch.yml)

## Installation of LeMATo 

* [Download and extract LeMATo](https://github.com/perdacherMartin/LeMATo/archive/master.zip)

# Run LeMATo 

* Before you start LeMATo via grails. You need to run the text search engine Elasticsearch: 
	* bin/elasticsearch on Unix or 
	* bin/elasticsearch.bat on Windows

* Switch to the LeMATo directory and enter `grails run-app`

* If everything is fine you should see something like: `Grails application running at http://localhost:8080 in environment: development`
