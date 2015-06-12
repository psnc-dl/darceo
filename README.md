### dArceo - long-term preservation tool 

dArceo is composed of a set of network services forming an extensible framework for long-term preservation purposes. The structure of a single, fully functional dArceo instance is presented in the figure and includes the following services:

![modules](https://cloud.githubusercontent.com/assets/5885929/7822575/d1366796-03f6-11e5-8bc5-d23dacfccba6.png)

**Source Data Manager** - responsible for storage and retrieval of source data. In addition, SDM performs automatic metadata extraction and provides versioning mechanisms for both the source data and its metadata. By default, SFTP and local file system can be used as the underlying storage, but the architecture of the SDM allows for easy integration with other data archiving systems via adapter plugins. Moreover, all operations are handled asynchronously, thus allowing the SDM to work with archives where the retrieval of data takes significant time. An internal part of SDM is the OAI-PMH repository, which provides the OAI-PMH interface for stored digital objects' metadata harvesting. It supports both the Dublin Core and METS formats.

**Data Manipulation Services** - allow the data to be migrated, converted or delivered in a different way. The framework provided by dArceo allows users to easily add new services and, since DMSs have a clearly defined communication interface, share them with other dArceo instances via a service registry. In addition to being used on their own, DMSs can be chained together, forming a complex data manipulation flow. There are three distinct groups of DMSs:
* Data Migration Services migrate the source data to a different format without losing information. They play a significant role in long-term data preservation.
* Data Conversion Services convert the data with some of the information lost in the process (e.g. lossy compression, resolution change, cropping). They are used to create presentation versions of digital objects, as the source data might not be suitable to be presented to the end user in its raw form.
* Advanced Data Delivery Services deliver the data in an effective, user-dependent way. For example, audiovisual data can be streamed, while large images, such as maps, can be served piece by piece as requested.

**Service Registry** - stores information about all registered DMSs. In addition to services added locally, SR can be also configured to harvest other SRs for their publicly available services and synchronise with the central SR. This allows users of a single dArceo instance to easily locate and access remote services, significantly increasing the number of available data manipulation options.

**Source Data Monitor** - periodically verifies the data integrity and assesses the risk of data loss in the context of long-term preservation. Data integrity is checked by calculating file checksums and comparing them to the values obtained when the particular file was added. In order to assess the risk of data loss, the module analyzes file formats currently used by stored objects and identifies those that might become unreadable in the near future (outdated formats without official support, for example). Corrupted files and file formats with high risk factor are reported to the administrator.

**System Monitor** - provides an overview of the dArceo instance in terms of performance, available resources and usage. Statistics are gathered per user, allowing the administrator to generate reports with a wide range of granularity.

**Data Migration and Conversion Manager** - allows users to create and execute complex data migration and conversion plans by chaining different data manipulation services. After a plan has been defined, DMCM handles its execution automatically, making even the most complex, multiple-step migrations and conversions easy to perform.

**Rights Manager** - allows the administrator to define which digital objects and services a particular user has access to.

**Notifications Manager** - provides the system with a unified channel for communication. Allows services to send messages to other services and the administrator in a simple way.


---

### Requirements & Installation

* Java SE Development Kit 7 with Java Cryptography Extension Unlimited Strength Jurisdiction Policy Files
* Glassfish Application Server 3.1.2
* PostgreSQL Server 9.0.3
* FFmpeg 2.6.1
* [FITS 0.8.3 with VideoMD extraction support](http://github.com/opf-labs/fits/tree/ffmpeg-videomd-aes)
* OWLIM-lite 5.2.5

Install the above mentioned software ([instruction](http://github.com/psnc-dl/darceo/blob/master/wrdz/wrdz-ear/src/main/install/README.txt)) and run the following [script](http://github.com/psnc-dl/darceo/blob/master/wrdz/wrdz-ear/src/main/install/darceo-install/darceo-install.sh). You may adjust variables in the [CSV file](http://github.com/psnc-dl/darceo/blob/master/wrdz/wrdz-ear/src/main/install/darceo-install/vars.csv).

Setting dArceo up on a different operating system than Linux is also possible. However, manual configuration of Glassfish, PostgreSQL, FITS and OWLIM-lite is necessary (according to steps performed by the bash script). You may also deploy each module on a separate Glassfish instance. 
