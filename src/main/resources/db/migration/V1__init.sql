-- 1. Authorities Table
CREATE TABLE authorities (
    id BIGINT NOT NULL AUTO_INCREMENT,
    authority VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 2. Channel Table
CREATE TABLE channel (
    member_count INTEGER NOT NULL,
    channel_id BIGINT NOT NULL,
    created_at DATETIME(6),
    deleted_at DATETIME(6),
    last_message_at DATETIME(6),
    last_message_id BIGINT,
    channel_name VARCHAR(100),
    last_message_content VARCHAR(100),
    PRIMARY KEY (channel_id)
) ENGINE=InnoDB;

-- 3. Channel Invite Sequence
CREATE TABLE channel_invite_seq (
    next_val BIGINT
) ENGINE=InnoDB;

INSERT INTO channel_invite_seq VALUES (1);

-- 4. Channel Member Table
CREATE TABLE channel_member (
    channel_id BIGINT NOT NULL,
    created_at DATETIME(6),
    deleted_at DATETIME(6),
    id BIGINT NOT NULL AUTO_INCREMENT,
    last_message_id BIGINT,
    last_read_message_id BIGINT,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 5. Channel Invite Table
CREATE TABLE channel_invite (
    channel_id BIGINT,
    created_at DATETIME(6),
    deleted_at DATETIME(6),
    id BIGINT NOT NULL,
    invitee_id BIGINT,
    inviter_id BIGINT,
    updated_at DATETIME(6),
    status ENUM ('ACCEPTED','DECLINED','PENDING'),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 6. Friend Request Sequence
CREATE TABLE friend_request_seq (
    next_val BIGINT
) ENGINE=InnoDB;

INSERT INTO friend_request_seq VALUES (1);

-- 7. Friend Request Table
CREATE TABLE friend_request (
    created_at DATETIME(6),
    deleted_at DATETIME(6),
    id BIGINT NOT NULL,
    invitee_id BIGINT,
    inviter_id BIGINT,
    updated_at DATETIME(6),
    status ENUM ('ACCEPTED','DECLINED','PENDING'),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 8. User Friend Table
CREATE TABLE user_friend (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_a_id BIGINT NOT NULL,
    user_b_id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 9. Users Table
CREATE TABLE users (
    channel_invite_count INTEGER NOT NULL,
    friend_count INTEGER NOT NULL,
    friend_request_count INTEGER NOT NULL,
    status TINYINT CHECK (status BETWEEN 0 AND 0),
    created_at DATETIME(6),
    deleted_at DATETIME(6),
    id BIGINT NOT NULL,
    login_id VARCHAR(255) NOT NULL,
    nickname VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    profile_icon_color VARCHAR(255),
    profile_image_url VARCHAR(255),
    username VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 10. Indexes & Constraints
CREATE INDEX idx_last_message_id ON channel (last_message_id DESC);
CREATE INDEX idx_user_last_msg ON channel_member (user_id, last_message_id DESC);

ALTER TABLE channel_member
    ADD CONSTRAINT uk_channel_member_channel_id_user_id UNIQUE (channel_id, user_id);

ALTER TABLE user_friend
    ADD CONSTRAINT uk_user_friend_users UNIQUE (user_a_id, user_b_id);

ALTER TABLE users
    ADD CONSTRAINT UKi3xs7wmfu2i3jt079uuetycit UNIQUE (login_id);

ALTER TABLE users
    ADD CONSTRAINT UKr43af9ap4edm43mmtq01oddj6 UNIQUE (username);

-- 11. Foreign Keys
ALTER TABLE authorities
    ADD CONSTRAINT FKhfk9xtwgn63wrudguq6xg7543
    FOREIGN KEY (id) REFERENCES users (id);

ALTER TABLE channel_member
    ADD CONSTRAINT FKc8s8yiekqn7aienyyd4vw87u8
    FOREIGN KEY (channel_id) REFERENCES channel (channel_id);

ALTER TABLE channel_member
    ADD CONSTRAINT FKkp8e3prtxod8xtixpst526vwv
    FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE channel_invite
    ADD CONSTRAINT FKkxfktryrhowin1yejh4dtxrdo
    FOREIGN KEY (channel_id) REFERENCES channel (channel_id);

ALTER TABLE channel_invite
    ADD CONSTRAINT FKiigcca1hw2hvofxpebtyuy5t6
    FOREIGN KEY (invitee_id) REFERENCES users (id);

ALTER TABLE channel_invite
    ADD CONSTRAINT FKetnxull6bgbi816oo64jm2ww0
    FOREIGN KEY (inviter_id) REFERENCES users (id);

ALTER TABLE friend_request
    ADD CONSTRAINT FKsq22kus2iwsirqk1g7ukkk8i7
    FOREIGN KEY (invitee_id) REFERENCES users (id);

ALTER TABLE friend_request
    ADD CONSTRAINT FK8uakuu0jkqfd3osusob5r4qgt
    FOREIGN KEY (inviter_id) REFERENCES users (id);

ALTER TABLE user_friend
    ADD CONSTRAINT FK5xny7c9lwag7xg93o74at7nkt
    FOREIGN KEY (user_a_id) REFERENCES users (id);

ALTER TABLE user_friend
    ADD CONSTRAINT FKfldoegh60lpg51ms7fbucrn51
    FOREIGN KEY (user_b_id) REFERENCES users (id);
