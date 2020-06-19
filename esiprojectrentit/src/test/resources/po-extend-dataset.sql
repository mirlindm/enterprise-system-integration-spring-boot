--Accept PO Extension

insert into plant_inventory_entry (id, name, description, price) values (5, 'Bulldozer', 'Bulldozer', 300);

insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (7, 5, 'A07', 'SERVICEABLE');

insert into PURCHASE_ORDER values (3, 'BUILDIT', 'esitartu2020@gmail.com', false, SYSDATE , SYSDATE ,to_date('20-JUL-2020', 'DD-MON-YYYY'), to_date('10-JUL-2020', 'DD-MON-YYYY'), 'ACCEPTED', 1000, null, 5);

insert into plant_reservation (id, end_date, start_date, maint_plan_id, plant_id, rental_id) values (1,to_date('20-JUL-2020', 'DD-MON-YYYY') ,to_date('10-JUL-2020', 'DD-MON-YYYY'),null,7,3);


--Reject PO Extension
insert into PURCHASE_ORDER values (4, 'BUILDIT', 'esitartu2020@gmail.com', false, SYSDATE , SYSDATE ,to_date('29-JUL-2020', 'DD-MON-YYYY'), to_date('26-JUL-2020', 'DD-MON-YYYY'), 'ACCEPTED', 1000, null, 5);

insert into plant_reservation (id, end_date, start_date, maint_plan_id, plant_id, rental_id) values (2,to_date('29-JUL-2020', 'DD-MON-YYYY') ,to_date('26-JUL-2020', 'DD-MON-YYYY'),null,7,4);

