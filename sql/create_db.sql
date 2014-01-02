CREATE SCHEMA mcplugin;
USE mcplugin;
CREATE USER mcplugin@'%' identified by 'mc123';
GRANT ALL ON mcplugin.* TO 'mcplugin'@'%';
GRANT ALL ON mcplugin TO 'mcplugin'@'%';
GRANT CREATE ON mcplugin TO 'mcplugin'@'%';
FLUSH PRIVILEGES;

create table warpstones (
  x int not null,
  y int not null,
  z int not null,
  world varchar(255) not null,
  signature varchar(255) default null,
  is_source tinyint(1),
  PRIMARY KEY(x,y,z,world)
);