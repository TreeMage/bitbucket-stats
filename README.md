## Bitbucket Stats
This is a project to try out Scala3 and the ZIO ecosystem a little more deeply. 
### What does it do?
I am not sure where this project will go yet. For now, it fetches pull request activity from Bitbucket and saves it to a database. <br>
In the future, I think I want to add some analytics capabilities on top of this data and then maybe add a frontend in ScalaJS?

### TODO
- [x] Only refetch activity for PRs that have been updated since the last fetch
- [x] Allow retrieving prs in any state
- [x] Add some logging