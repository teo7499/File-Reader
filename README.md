# File-Reader
A small project to test out reading file directories and some simple mongoDB operations using Mongo Scala Driver. I didn't add the any user entry for this script but you could easily add it. The only issue that could happen might be read-protected files/directories./br></br>
You can run this programme if you have SBT to compile and build.

<b>Interesting bits</b></br>
Used stream to handle the collection of files as stream will allow elements in the list to be computed lazily(when it is needed). Just in case the directory has a huge number of files, this will save some computing power and avoid out of memory error although the chances of it happening is incredibly minute.
