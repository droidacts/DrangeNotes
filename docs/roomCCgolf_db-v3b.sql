BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS `Shots` (
`dist` REAL NOT NULL, 
`power` INTEGER, 
`stick` INTEGER NOT NULL, 
`sshape` INTEGER, 
`golfer` INTEGER, 
`loc` TEXT, 
`comment` TEXT, 
`sn` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
`datetime` INTEGER, 
FOREIGN KEY(`stick`) REFERENCES `Sticks`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION , 
FOREIGN KEY(`golfer`) REFERENCES `Golfers`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION , FOREIGN KEY(`power`) REFERENCES `Plvl`(`idx`) ON UPDATE NO ACTION ON DELETE NO ACTION , 
FOREIGN KEY(`sshape`) REFERENCES `Traj`(`type`) ON UPDATE NO ACTION ON DELETE NO ACTION 
);
CREATE TABLE IF NOT EXISTS `Golfers` (
`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
`name` TEXT NOT NULL, `info` TEXT
);
CREATE TABLE IF NOT EXISTS `Plvl` (
`idx` INTEGER NOT NULL, 
`desc` TEXT, 
PRIMARY KEY(`idx`)
);
CREATE TABLE IF NOT EXISTS `Sticks` (
`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
`title` TEXT NOT NULL, 
`desc` TEXT, 
`specs` TEXT
);
CREATE TABLE IF NOT EXISTS `Traj` (
`type` INTEGER NOT NULL, 
`desc` TEXT, `icon` INTEGER, 
PRIMARY KEY(`type`)
);
CREATE TABLE IF NOT EXISTS `Bags` (
`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
`golfer` INTEGER NOT NULL DEFAULT 0, 
`desc` TEXT, 
`s01` INTEGER NOT NULL, 
`s02` INTEGER, `s03` INTEGER, `s04` INTEGER, `s05` INTEGER, `s06` INTEGER, `s07` INTEGER, `s08` INTEGER, `s09` INTEGER, `s10` INTEGER, `s11` INTEGER, `s12` INTEGER, `s13` INTEGER, `s14` INTEGER, 
FOREIGN KEY(`s01`) REFERENCES `Sticks`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION , 
FOREIGN KEY(`golfer`) REFERENCES `Golfers`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION 
);
INSERT INTO "Golfers" ("id","name","info") VALUES (0,'who','Defaultee');
INSERT INTO "Golfers" ("id","name","info") VALUES (1,'CC','The originator');
INSERT INTO "Golfers" ("id","name","info") VALUES (2,'BB','Tester');
INSERT INTO "Golfers" ("id","name","info") VALUES (3,'others',NULL);
INSERT INTO "Plvl" ("idx","desc") VALUES (0,'any');
INSERT INTO "Plvl" ("idx","desc") VALUES (1,'Chip');
INSERT INTO "Plvl" ("idx","desc") VALUES (2,'1/2');
INSERT INTO "Plvl" ("idx","desc") VALUES (3,'3/4');
INSERT INTO "Plvl" ("idx","desc") VALUES (4,'Full');
INSERT INTO "Sticks" ("id","title","desc","specs") VALUES (1,'Lw','generic',NULL);
INSERT INTO "Sticks" ("id","title","desc","specs") VALUES (2,'Sw','generic',NULL);
INSERT INTO "Sticks" ("id","title","desc","specs") VALUES (3,'Gw','generic',NULL);
INSERT INTO "Sticks" ("id","title","desc","specs") VALUES (4,'Pw','generic',NULL);
INSERT INTO "Sticks" ("id","title","desc","specs") VALUES (5,'9i','generic',NULL);
INSERT INTO "Sticks" ("id","title","desc","specs") VALUES (6,'8i','generic',NULL);
INSERT INTO "Sticks" ("id","title","desc","specs") VALUES (7,'7i','generic',NULL);
INSERT INTO "Sticks" ("id","title","desc","specs") VALUES (8,'6i','generic',NULL);
INSERT INTO "Sticks" ("id","title","desc","specs") VALUES (9,'5i','generic',NULL);
INSERT INTO "Sticks" ("id","title","desc","specs") VALUES (10,'4i','generic',NULL);
INSERT INTO "Sticks" ("id","title","desc","specs") VALUES (11,'3H','generic',NULL);
INSERT INTO "Sticks" ("id","title","desc","specs") VALUES (12,'2H','generic',NULL);
INSERT INTO "Sticks" ("id","title","desc","specs") VALUES (13,'3/5W','generic',NULL);
INSERT INTO "Sticks" ("id","title","desc","specs") VALUES (14,'Driver','generic',NULL);
INSERT INTO "Sticks" ("id","title","desc","specs") VALUES (15,'Putt','generic',NULL);
INSERT INTO "Traj" ("type","desc","icon") VALUES (0,'N/A',NULL);
INSERT INTO "Traj" ("type","desc","icon") VALUES (1,'left',NULL);
INSERT INTO "Traj" ("type","desc","icon") VALUES (2,'right',NULL);
INSERT INTO "Traj" ("type","desc","icon") VALUES (3,'straight',NULL);
INSERT INTO "Traj" ("type","desc","icon") VALUES (4,'miss',NULL);
INSERT INTO "Traj" ("type","desc","icon") VALUES (8,'curve',NULL);
INSERT INTO "Traj" ("type","desc","icon") VALUES (16,'curve++',NULL);
INSERT INTO "Bags" ("id","golfer","desc","s01","s02","s03","s04","s05","s06","s07","s08","s09","s10","s11","s12","s13","s14") VALUES (1,0,'default',14,12,11,10,9,8,7,6,5,4,3,2,1,15);
INSERT INTO "Bags" ("id","golfer","desc","s01","s02","s03","s04","s05","s06","s07","s08","s09","s10","s11","s12","s13","s14") VALUES (2,0,'Range',14,13,12,11,10,9,8,7,6,5,4,3,2,1);
COMMIT;
