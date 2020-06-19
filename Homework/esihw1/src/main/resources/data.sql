//insert into plant_inventory_entry (id, name, description, price)
//    values (1, 'Mini excavator', '1.5 Tonne Mini excavator', 150);
//insert into plant_inventory_entry (id, name, description, price)
//    values (2, 'Mini excavator', '3 Tonne Mini excavator', 200);
//insert into plant_inventory_entry (id, name, description, price)
//    values (3, 'Midi excavator', '5 Tonne Midi excavator', 250);
//insert into plant_inventory_entry (id, name, description, price)
//    values (4, 'Midi excavator', '8 Tonne Midi excavator', 300);
//insert into plant_inventory_entry (id, name, description, price)
//    values (5, 'Maxi excavator', '15 Tonne Large excavator', 400);
//insert into plant_inventory_entry (id, name, description, price)
//    values (6, 'Maxi excavator', '20 Tonne Large excavator', 450);
//insert into plant_inventory_entry (id, name, description, price)
//    values (7, 'HS dumper', '1.5 Tonne Hi-Swivel Dumper', 150);
//insert into plant_inventory_entry (id, name, description, price)
//    values (8, 'FT dumper', '2 Tonne Front Tip Dumper', 180);
//insert into plant_inventory_entry (id, name, description, price)
//    values (9, 'FT dumper', '2 Tonne Front Tip Dumper', 200);
//insert into plant_inventory_entry (id, name, description, price)
//    values (10, 'FT dumper', '2 Tonne Front Tip Dumper', 300);
//insert into plant_inventory_entry (id, name, description, price)
//    values (11, 'FT dumper', '3 Tonne Front Tip Dumper', 400);
//insert into plant_inventory_entry (id, name, description, price)
//    values (12, 'Loader', 'Hewden Backhoe Loader', 200);
//insert into plant_inventory_entry (id, name, description, price)
//    values (13, 'D-Truck', '15 Tonne Articulating Dump Truck', 250);
//insert into plant_inventory_entry (id, name, description, price)
//    values (14, 'D-Truck', '30 Tonne Articulating Dump Truck', 300);
//
//insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition)
//    values (1, 1, 'A01', 'SERVICEABLE');
//insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition)
//    values (2, 2, 'A02', 'SERVICEABLE');
//insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition)
//    values (8, 3, 'A03', 'UNSERVICEABLEREPAIRABLE');
//
/////////////////////////////////////
//// Custom Data for Query Testing //
////////////////////////////////////
//
////insert into MAINTENANCE_PLAN values (1, 2020, 1);
////
////insert into plant_reservation (id, plant_id, start_date, end_date,maint_plan_id) values (1, 1, '2017-03-22', '2017-03-24',1);
////insert into MAINTENANCE_TASK values (1, 'first maintenance task', '2017-03-24' ,'2017-03-22' , 1000 , 'renew pla pla pla' , 1);
////insert into MAINTENANCE_PLAN_TASKS values (1,1);
////
////insert into PURCHASE_ORDER values (1, '2017-03-22', '2017-05-20', '2017-03-21' , '2017-03-18' , 'OPEN' , 2000 , 1);
////insert into plant_reservation (id, plant_id, start_date, end_date,rental_id) values (2, 1, '2017-03-18', '2017-03-21' , 1);
////insert into PURCHASE_ORDER_RESERVATIONS values (1,2);
////
////insert into plant_reservation (id, plant_id, start_date, end_date,maint_plan_id) values (3, 1, '2017-03-10', '2017-03-15',1);
////insert into MAINTENANCE_TASK values (2, 'second maintenance task', '2017-03-15' ,'2017-03-10' , 1000 , 'renew pla pla pla' , 3);
////insert into MAINTENANCE_PLAN_TASKS values (1,2);
////
////insert into PURCHASE_ORDER values (2, '2017-03-22', '2017-05-20', '2017-03-05' , '2017-03-01' , 'OPEN' , 2000 , 1);
////insert into plant_reservation (id, plant_id, start_date, end_date,rental_id) values (4, 1, '2017-03-01', '2017-03-05' , 2);
////insert into PURCHASE_ORDER_RESERVATIONS values (2,4);
//
//
//insert into MAINTENANCE_PLAN values (1, 2020, 1);
//insert into plant_reservation (id, plant_id, start_date, end_date,maint_plan_id) values (1, 7, '2020-03-11', '2020-03-13',1);
//insert into MAINTENANCE_TASK (id, DESCRIPTION, START_DATE, END_DATE, TOTAL, TYPE_OF_WORK, MAINTENANCE_PLAN_ID, RESERVATION_ID)values (1, 'first maintenance task', '2020-03-11' ,'2020-03-13' , 1000 , 'renew pla pla pla' ,1, 1);
