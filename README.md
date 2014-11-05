DAO-Android
=============

Data access object (DAO) abstracts all data persistence, cache and access for application layer. Data is cached in heap and persisted in disk and optionally in cloud.

MingDAO - handles data in the heap cache and interfaces with application layer. Unless your application is very simple and one DAO is good enough, you normally need to create multiple DAO classes (as singleton) which must subclass from MingDAO.

MingStore (MingStoreWithxxx) - handles data in the local disk, currently supports saving data in preference, property list, file and SQL lite. You can provide other means for disk storage, but they all need to conform to MingStore protocol.

MingCloud - handles data in backend cloud. You need provide concrete class to make it work, for example hook in Parse backend.

INSTALLATION
-------------
DAO is not provided as a JAR library. To install, just add all these Java files into your project, and make sure add package to every class accordingly.

