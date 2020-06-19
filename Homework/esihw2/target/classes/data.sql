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

-- insert into MAINTENANCE_PLAN values (1, 2019, 1);
-- insert into MAINTENANCE_PLAN values (2, 2020, 1);
-- insert into MAINTENANCE_PLAN values (3, 2019, 2);
-- insert into MAINTENANCE_PLAN values (4, 2020, 2);
--
-- insert into plant_reservation (id, plant_id, start_date, end_date,maint_plan_id) values (1, 1, '2019-07-10', '2019-07-20',1);
-- insert into MAINTENANCE_TASK (id, DESCRIPTION, START_DATE, END_DATE, TOTAL, TYPE_OF_WORK, MAINTENANCE_PLAN_ID, RESERVATION_ID)
-- 	values (1, 'first maintenance task', '2019-07-10', '2019-07-20' , 1000 , 'PREVENTIVE' ,1, 1);
--
-- insert into plant_reservation (id, plant_id, start_date, end_date,maint_plan_id) values (2, 1, '2019-08-10', '2019-08-20',1);
-- insert into MAINTENANCE_TASK (id, DESCRIPTION, START_DATE, END_DATE, TOTAL, TYPE_OF_WORK, MAINTENANCE_PLAN_ID, RESERVATION_ID)
-- 	values (2, 'second maintenance task', '2019-08-10', '2019-08-20' , 1000 , 'PREVENTIVE' ,1, 2);
--
-- insert into plant_reservation (id, plant_id, start_date, end_date,maint_plan_id) values (3, 1, '2020-04-01', '2020-04-25',2);
-- insert into MAINTENANCE_TASK (id, DESCRIPTION, START_DATE, END_DATE, TOTAL, TYPE_OF_WORK, MAINTENANCE_PLAN_ID, RESERVATION_ID)
-- 	values (3, 'planned maintenance task', '2020-04-01', '2020-04-25' , 1000 , 'PREVENTIVE' ,2, 3);
--
-- insert into plant_reservation (id, plant_id, start_date, end_date,maint_plan_id) values (4, 1, '2020-06-01', '2020-06-25',2);
-- insert into MAINTENANCE_TASK (id, DESCRIPTION, START_DATE, END_DATE, TOTAL, TYPE_OF_WORK, MAINTENANCE_PLAN_ID, RESERVATION_ID)
-- 	values (4, 'planned maintenance task', '2020-06-01', '2020-06-25' , 1000 , 'PREVENTIVE' ,2, 4);
--
--
-- insert into plant_reservation (id, plant_id, start_date, end_date,maint_plan_id) values (5, 2, '2019-07-10', '2019-07-20',3);
-- insert into MAINTENANCE_TASK (id, DESCRIPTION, START_DATE, END_DATE, TOTAL, TYPE_OF_WORK, MAINTENANCE_PLAN_ID, RESERVATION_ID)
-- 	values (5, 'first maintenance task', '2019-07-10', '2019-07-20' , 1000 , 'PREVENTIVE' ,3, 5);
--
-- insert into plant_reservation (id, plant_id, start_date, end_date,maint_plan_id) values (6, 2, '2019-08-10', '2019-08-20',3);
-- insert into MAINTENANCE_TASK (id, DESCRIPTION, START_DATE, END_DATE, TOTAL, TYPE_OF_WORK, MAINTENANCE_PLAN_ID, RESERVATION_ID)
-- 	values (6, 'second maintenance task', '2019-08-10', '2019-08-20' , 1000 , 'PREVENTIVE' ,3, 6);
--
-- insert into plant_reservation (id, plant_id, start_date, end_date,maint_plan_id) values (7, 2, '2020-04-01', '2020-04-25',4);
-- insert into MAINTENANCE_TASK (id, DESCRIPTION, START_DATE, END_DATE, TOTAL, TYPE_OF_WORK, MAINTENANCE_PLAN_ID, RESERVATION_ID)
-- 	values (7, 'planned maintenance task', '2020-04-01', '2020-04-25' , 1000 , 'PREVENTIVE' ,4, 7);
--
-- insert into plant_reservation (id, plant_id, start_date, end_date,maint_plan_id) values (8, 2, '2020-06-01', '2020-06-25',4);
-- insert into MAINTENANCE_TASK (id, DESCRIPTION, START_DATE, END_DATE, TOTAL, TYPE_OF_WORK, MAINTENANCE_PLAN_ID, RESERVATION_ID)
-- 	values (8, 'planned maintenance task', '2020-06-01', '2020-06-25' , 1000 , 'PREVENTIVE' ,4, 8);


-- /////////////////////////////////////
-- //// Custom Data for Query Testing //
-- ////////////////////////////////////
-- //
-- ////insert into MAINTENANCE_PLAN values (1, 2020, 1);
-- ////
-- ////insert into plant_reservation (id, plant_id, start_date, end_date,maint_plan_id) values (1, 1, '2017-03-22', '2017-03-24',1);
-- ////insert into MAINTENANCE_TASK values (1, 'first maintenance task', '2017-03-24' ,'2017-03-22' , 1000 , 'renew pla pla pla' , 1);
-- ////insert into MAINTENANCE_PLAN_TASKS values (1,1);
-- ////
-- ////insert into PURCHASE_ORDER values (1, '2017-03-22', '2017-05-20', '2017-03-21' , '2017-03-18' , 'OPEN' , 2000 , 1);
-- ////insert into plant_reservation (id, plant_id, start_date, end_date,rental_id) values (2, 1, '2017-03-18', '2017-03-21' , 1);
-- ////insert into PURCHASE_ORDER_RESERVATIONS values (1,2);
-- ////
-- ////insert into plant_reservation (id, plant_id, start_date, end_date,maint_plan_id) values (3, 1, '2017-03-10', '2017-03-15',1);
-- ////insert into MAINTENANCE_TASK values (2, 'second maintenance task', '2017-03-15' ,'2017-03-10' , 1000 , 'renew pla pla pla' , 3);
-- ////insert into MAINTENANCE_PLAN_TASKS values (1,2);
-- ////
-- ////insert into PURCHASE_ORDER values (2, '2017-03-22', '2017-05-20', '2017-03-05' , '2017-03-01' , 'OPEN' , 2000 , 1);
-- ////insert into plant_reservation (id, plant_id, start_date, end_date,rental_id) values (4, 1, '2017-03-01', '2017-03-05' , 2);
-- ////insert into PURCHASE_ORDER_RESERVATIONS values (2,4);
-- //
-- //
-- //insert into MAINTENANCE_PLAN values (1, 2020, 1);
-- //insert into plant_reservation (id, plant_id, start_date, end_date,maint_plan_id) values (1, 7, '2020-03-11', '2020-03-13',1);
-- //insert into MAINTENANCE_TASK (id, DESCRIPTION, START_DATE, END_DATE, TOTAL, TYPE_OF_WORK, MAINTENANCE_PLAN_ID, RESERVATION_ID)values (1, 'first maintenance task', '2020-03-11' ,'2020-03-13' , 1000 , 'renew pla pla pla' ,1, 1);
