@echo off
set ver=%1
mvn install:install-file -DgroupId=de.bethibande -DartifactId=jsql -Dversion=%ver% -Dfile=../JSQL.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true
git add .
git commit -m "Released version %ver%"
git push origin repository
ECHO Finished releasing version %ver%!
PAUSE