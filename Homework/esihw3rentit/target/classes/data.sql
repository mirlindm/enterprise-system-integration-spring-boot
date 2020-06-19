insert into plant_inventory_entry (id, name, description, price) values (1, 'Mini excavator', '1.5 Tonne Mini excavator', 150);
insert into plant_inventory_entry (id, name, description, price) values (2, 'Mini excavator', '3 Tonne Mini excavator', 200);
insert into plant_inventory_entry (id, name, description, price) values (3, 'Midi excavator', '5 Tonne Midi excavator', 250);
insert into plant_inventory_entry (id, name, description, price) values (4, 'Midi excavator', '8 Tonne Midi excavator', 300);

insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (1, 1, 'A01', 'SERVICEABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (2, 1, 'A02', 'SERVICEABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (3, 2, 'A03', 'SERVICEABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (4, 2, 'A04', 'UNSERVICEABLECONDEMNED');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (5, 2, 'A05', 'UNSERVICEABLEREPAIRABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (6, 2, 'A06', 'UNSERVICEABLEINCOMPLETE');

insert into PURCHASE_ORDER values (1, SYSDATE , SYSDATE ,SYSDATE, SYSDATE, 'PENDING', 1000, 1);
insert into PURCHASE_ORDER values (2, SYSDATE , SYSDATE ,SYSDATE, SYSDATE, 'OPEN', 1000, 1);

-- insert into MAINTENANCE_ORDER values (1, 1, 'DESC1', SYSDATE, SYSDATE, SYSDATE, 'ENG 1', 'PENDING', 1);
-- insert into MAINTENANCE_ORDER values (2, 1, 'DESC2', SYSDATE, SYSDATE, SYSDATE, 'ENG 2', 'PENDING', 2);
-- insert into MAINTENANCE_ORDER values (3, 1, 'DESC3', SYSDATE, SYSDATE, SYSDATE, 'ENG 3', 'PENDING', 3);
-- insert into MAINTENANCE_ORDER values (4, 1, 'DESC4', SYSDATE, SYSDATE, SYSDATE, 'ENG 4', 'PENDING', 4);
-- insert into MAINTENANCE_ORDER values (5, 1, 'DESC5', SYSDATE, SYSDATE, SYSDATE, 'ENG 5', 'PENDING', 5);