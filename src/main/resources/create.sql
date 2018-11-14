# �������ݿ�
CREATE DATABASE ruian;

USE ruian;
# ��������Ա��
CREATE TABLE admin(
  `id` int NOT NULL AUTO_INCREMENT COMMENT '�û�id',
  `name` VARCHAR(20) COMMENT '�û���',
  `password` VARCHAR(20) COMMENT '�û�����',
  PRIMARY KEY (id)
)ENGINE = InnoDB CHARSET = utf8;

# ��������Ŀ��
CREATE TABLE supermenu(
  `id` int NOT NULL AUTO_INCREMENT COMMENT '����Ŀid',
  `name` VARCHAR(20) COMMENT '��Ŀ��',
  PRIMARY KEY (id)
)ENGINE = InnoDB CHARSET = utf8;

# ��������Ŀ��
CREATE TABLE menu(
  `id` int NOT NULL AUTO_INCREMENT COMMENT '����Ŀid',
  `name` VARCHAR(20) COMMENT '��Ŀ��',
  `super_menu_id` int not null comment '����Ŀid',
  PRIMARY KEY (id)
)ENGINE = InnoDB CHARSET = utf8;

# ������Ϣ��
CREATE TABLE message(
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` VARCHAR(50) COMMENT '����',
  `title_img` VARCHAR(20) COMMENT '����ͼ·��',
  `date` varchar(20) COMMENT '��������',
  `content` TEXT COMMENT '���ı�����',
  `file` VARCHAR(100) COMMENT '����·��',
  `is_push` tinyint COMMENT '�Ƿ�����',
  `menu_id` INT COMMENT '��Ŀid',
  `admin_id` INT COMMENT '�����˵�����',
  PRIMARY KEY (id)
)ENGINE = InnoDB CHARSET = utf8;

# �������Լ��
ALTER TABLE message ADD CONSTRAINT fk_admin_id FOREIGN KEY (admin_id) REFERENCES admin(id);
ALTER TABLE message ADD CONSTRAINT fk_column_id FOREIGN KEY (menu_id) REFERENCES menu(id);
ALTER TABLE menu ADD CONSTRAINT fk_super_munu_id FOREIGN KEY (super_menu_id) REFERENCES supermenu(id);