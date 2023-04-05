# MySQL Connector

```ruby
DatabaseInfo info = new DatabaseInfo("localhost", 3306, "website", "username", "secret");
DatabaseManager manager = new DatabaseManager(info);
manager.createTable("groups").addString("uuid").addString("name").addString("permissions").create();
Table groups = manager.getTable("groups", "uuid");
UUID uuid = UUID.randomUUID();
String name = groups.get("name", uuid.toString()).get().asString();
System.out.println(name);
manager.shutdown();
```
