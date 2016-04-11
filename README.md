# gitSvnSync - SVN <===> GIT Synchronization
Of course there are options to move in one big step from SVN to GIT! ... and of course - for a team that is fit in GIT
thats the way you should go with. But in a real world scenarios it is in most cases a bit different!

There are some developers with no or less experience. Okay - it is easy to convince them. But if you do the big bang
move thae feel not very comfortable and you need them to convince the management as well! That is the obstacle you
should focus on! To take that you need some time the support from all the team members...

## The biggest advantages
There are some advantages of using a step wise approach:

 * you never loose the ability to deliver with the old tools
 * team members can join the GIT side when ever they want
 * you can support team members on the way to GIT one after each other
 * the number of supporters will grow after they get some problems solved
 * you have the time to create the get the pipeline ready and tested
 * at the end you simply switch off the synchronization

## The steps to do
### clone that project
### run maven package in the root folder
    mvn package
### create the shared repository (central repository approach)
Create an empty shared repository.

    cd /some/nice/path/to/your/SHARED_repositories
    git init --shared project_foo.git

### create the synchronization repository
The repository where the synchronization is working on has to be hidden from the rest of the users! Nobody should work
on that! The only purpose is to provide the synchronization to SVN.

    git svn clone \
       --trunk=/$SVN_SOURCE_BRANCH \
       --authors-file=users.txt \
       --prefix=svn \
       --ignore-paths="$ignored_modules" \
       -r $SVN_ROOT_REVISION_NO \
       $SVN_REPOSITORY_BASE/ \
       $REPOSITORY_PATH_FOR_SYNCHRONIZATION/$PROJECT_NAME.git

   If you want to do a dry run first, you should create a svn branch to work with and than use the flag

     --trunk=/$SVN_SOURCE_BRANCH

   ... where '$SVN_SOURCE_BRANCH' is your branch name.

   The flag '-r' is for setting the start point in the history. It is a SVN revision number you want to start with.
   The history before that revision number will not be part of the target GIT repository. '$SVN_ROOT_REVISION_NO' is the
   value.

   If you have some modules you don't want to have in your GIT repository you  can provide them as the value of
   '$ignored_modulesThe'. The most important things are
    '$SVN_REPOSITORY_BASE' and '$REPOSITORY_PATH_FOR_SYNCHRONIZATION/$PROJECT_NAME.git'.
    With '$SVN_REPOSITORY_BASE' you specify the SVN source and with the second the repository location.

   The file 'users.txt' contains all the details GIT needs to create the GIT commits for all SVN commits.

    [username] = [name the user should have in GIT] <the_email@the_company.com>

   It is a simple list with key value pairs on each line. The key is the username and the value is the combination of
   username and email address.

### adjust the properties
Have a look at the file 'src/main/resources/application.properties'. There are not so many properties and each of them
is explained there.

### start the application and have a look into the logs
You can start the application with:

    java -Xmx256m -jar GitSvnSyncService.jar --security.user.name=admin --security.user.password=passwd123
    
After the application starts you will see a log file sync.log. Have a look into that file to see if all the things are
working as expected. If you face some problems write me a mail! I will try to respond as soon as possible.

