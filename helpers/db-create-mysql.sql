-- MySql
CREATE TABLE IF NOT EXISTS `attributes` (
  `id` int(11) NOT NULL,
  `type` enum('Simple','Simple numeric','Multi-valued','Multi-valued numeric','Repeating','Repeating numeric') NOT NULL,
  `name` varchar(255) NOT NULL,
  `deactivated` boolean NOT NULL DEFAULT FALSE,
  `group_id` int(11) unsigned NOT NULL,
  `group_name` varchar(255) NOT NULL,
  `last_update` date NOT NULL,
  `comment` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;