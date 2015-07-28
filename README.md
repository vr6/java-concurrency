# java-concurrency
```
EXIF Parser

  What is it?
  -----------
  The EXIF Parser is a batch processing application for parsing large
  image metadata files in EXIF (XML) format as input and generating html files 
  that have thumbnails and navigation links to browse the images, as output.
  
  Installation & running
  ----------------------
  Extract the ZIP/RAR file into an installation directory.
  Depending on the OS, run the following command from the install directory.
  
	Windows:        run.bat <input file location> <output path>
	Other OS:       run.sh <input file location> <output path>  
  
  where <input file location> is the location of EXIF data file (XML)
  and <output path> is the directory for the generated html files.
  
  Languages and Libraries used
  ----------------------------
     o Java/JRE (version 1.8.0_45)
     o Maven (version 3.2.5)
     o SQLite JDBC (version 3.8.7)
     o Log4j (version 1.2.17)
     o JUnit (version 3.8.1)
     
  Design considerations
  ---------------------
     o XML parsing using the JRE in-built StAX parser
     
     o An embedded SQL database is used to make the design and code 
       more scalable and maintainable. The application required selecting
       image data that matches specific make and/or model. The other approach
       I have considered was an XSLT processor. However I saw that the code was 
       getting too complex with XSLT, favoring the SQL approach.
     
     o Java concurrency utils are used for multi-threading
     
     o Multi-threading is used only for writing html files
     
     o The work of writing html files is divided across threads
       based on the camera make. In a real-world scenario, I would 
       probably use a different strategy such as using hadoop for 
       running map-reduce tasks and a compute cluster.
     
     o Multi-threading did not yield great results for inserting data.
       So the SQLite inserts are using single thread (main thread).
       In a real-world case, I would use PostgreSQL for multi-threading.
       
     o I have designed a developed a simple data access layer for
       this work. It is possible to make it more generic and extend to 
       support different usecases.
       
  Development practices
  --------------------
  Performance & Modernity
     o Parallelizing the processing using Java concurrency
     o Database optimization such as reuse of statements
     o Use of Java 8 features

  Code maintainability
     o Highly modular components with smaller methods
     o Each component has a single well-defined responsibility
     o Build & packaging using maven
     o Code structure using Maven conventions
  
  Extra features
     o Thumbnails as links for larger images
     o Uniform height for thumbnail images
     o Readability for the generated html files
  
  Monitoring & Troubleshooting
     o Logging using Log4J 
     o Instrumentation for basic profiling
     o Input validation
     o Error handling

  Testing
     o Automated testing using maven and JUnit

  Documentation
     o Readme and Design documentation (this file)
     o Documentation inside code
  
  Contacts
  --------
     o Author: Venkat Reddy (venkat@apache.org)
```
