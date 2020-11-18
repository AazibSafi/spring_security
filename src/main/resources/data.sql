INSERT INTO users (id, user_name, email, password)
VALUES (1,'john','john@gmail.com','$2a$04$bfPGrBQkcZ3jdp0WYNG/C.UbiqncZM4rKtpKE0KTzoFVHKzJdzKqi'),
       (2,'david','david@gmail.com','$2a$04$bfPGrBQkcZ3jdp0WYNG/C.UbiqncZM4rKtpKE0KTzoFVHKzJdzKqi'),
       (3,'ali','ali@gmail.com','$2a$04$bfPGrBQkcZ3jdp0WYNG/C.UbiqncZM4rKtpKE0KTzoFVHKzJdzKqi');


INSERT INTO roles (id, role_name, description)
VALUES (1, 'ADMIN', 'User Admin Role'),
       (2, 'MANAGER', 'User Manager Role'),
       (3, 'EMPLOYEE', 'User Employee Role');


INSERT  INTO user_roles (user_id, role_id) VALUES (1,1), (2,2), (3,3);
