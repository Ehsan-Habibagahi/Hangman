# Hangman
## Quick guide for database creation
Restore the file `hangman.sql` which is dumped by `Mysqldump` tool
1. Create database<br>
   `CREATE DATABASE hangman`
2. Restore backup <br>
`mysql -u root -p hangman < hangman.sql`<br>
in unix-based shell or <br>
`Get-Content .\hangman.sql | mysql -u root -p hangman`
<br>for windows
