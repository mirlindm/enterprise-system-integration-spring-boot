package com.example.demo.models;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryRepositoryImpl implements CustomInventoryRepository {

    @Autowired
    EntityManager em;

    public List<PlantInventoryEntry> findAvailablePlants(String name, LocalDate startDate, LocalDate endDate) {
        return em.createQuery("select p.plantInfo from PlantInventoryItem p where p.plantInfo.name like concat('%', ?1, '%') and p not in" +
                        "(select r.plant from PlantReservation r where ?2 < r.schedule.endDate and ?3 > r.schedule.startDate)",
                PlantInventoryEntry.class)
                .setParameter(1, name)
                .setParameter(2, startDate)
                .setParameter(3, endDate)
                .getResultList();
    }

    public List<Pair<String, BigDecimal>> query1() {
        List<Object[]> output = em.createNativeQuery("select name, \n" +
                "\t\tsum(select nvl(sum(nvl(total,0)),0) \n" +
                "\t\t\t\tfrom PURCHASE_ORDER po \n" +
                "\t\t\t\t\tinner join PLANT_RESERVATION pr on po.id = pr.rental_id \n" +
                "\t\t\t\twhere pr.plant_id = pii.id \n" +
                "\t\t\t\tand po.end_date >= (sysdate - 365) \n" +
                "\t\t\t\tand po.start_date >= (sysdate - 365))  \n" +
                "\t\t\t- sum(select nvl(sum(nvl(total,0)),0) \n" +
                "\t\t\t\t  from MAINTENANCE_TASK mt \n" +
                "\t\t\t\t\tinner join PLANT_RESERVATION pr on pr.id = mt.RESERVATION_ID \n" +
                "\t\t\t\t  where pr.plant_id = pii.id \n" +
                "\t\t\t\t  and mt.END_DATE >= (sysdate - 365) \n" +
                "\t\t\t\t  and mt.start_date >= (sysdate - 365)) revenue_12m \n" +
                "from PLANT_INVENTORY_ENTRY pie \n" +
                "\tleft join PLANT_INVENTORY_ITEM pii \n" +
                "\t\ton pie.id = pii.PLANT_INFO_ID \n" +
                "group by name")
                .getResultList();

        List<Pair<String, BigDecimal>> l = new ArrayList<Pair<String, BigDecimal>>();

        for (Object[] obj : output) {

            String name = (String)obj[0];
            BigDecimal bg = (BigDecimal)obj[1];
            Pair<String, BigDecimal> pair = new Pair<>(name, bg);
            l.add(pair);
        }

        return l;
    }


    public List<Pair<String, Long>> query2(LocalDate startDate, LocalDate endDate) {
        List<Object[]> output = em.createNativeQuery("select name, DATEDIFF(DAY, ?1 , ?2) - sum(nvl(DATEDIFF(DAY,start_date , end_date),0)) diff\n" +
                "from (\n" +
                "select pie.name,  case when pr.start_date < ?1 and pr.end_date between ?1 and ?2 then ?1 else pr.start_date end as start_date , case when pr.end_date > ?2 and pr.start_date between ?1 and ?2 then ?2 else pr.end_date end as end_date\n" +
                "from plant_inventory_entry pie \n" +
                "\tleft join plant_inventory_item pii \n" +
                "\t\ton pie.id = pii.plant_info_id \n" +
                "\tleft join plant_reservation pr \n" +
                "\t\ton pii.id = pr.plant_id \n" +
                "\t\tand ((pr.start_date between ?1 and ?2) or (pr.end_date between ?1 and ?2))\n" +
                ")\n" +
                "group by name")
                .setParameter(1, startDate)
                .setParameter(2, endDate)
                .getResultList();

        List<Pair<String, Long>> l = new ArrayList<Pair<String, Long>>();

        for (Object[] obj : output) {

            String name = (String)obj[0];
            BigDecimal bg = (BigDecimal)obj[1];
            Long count = bg.longValue();
            Pair<String, Long> pair = new Pair<>(name, count);
            l.add(pair);
        }

        return l;
    }

    public List<Pair<String, Long>> query3(String plantName, LocalDate startDate, LocalDate endDate) {
        List<Object[]> output = em.createNativeQuery("select pie.name, count(pii.id) \n" +
                "from PLANT_INVENTORY_ENTRY pie \n" +
                "\tleft join PLANT_INVENTORY_ITEM pii \n" +
                "\t\ton pie.id = pii.PLANT_INFO_ID \n" +
                "where upper(pie.name) like upper(concat('%', ?1, '%')) \n" +
                "and pii.EQUIPMENT_CONDITION <> 'UNSERVICEABLECONDEMNED' \n" +
                "and ((select count(pr.id) from PLANT_RESERVATION pr where pr.plant_id = pii.id and ((pr.start_date between ?2 and ?3) or (pr.end_date between ?2 and ?3) or (pr.start_date <= ?2 and pr.end_date >= ?3))) = 0)\n" +
                "and (pii.EQUIPMENT_CONDITION = 'SERVICEABLE' \n" +
                "\tor (DATEDIFF(DAY, sysdate , ?2 ) > 21 \n" +
                "\t\tand ((select count(nvl(mt.id,0)) \n" +
                "\t\t\t  from PLANT_RESERVATION pr \n" +
                "\t\t\t  inner join MAINTENANCE_TASK mt \n" +
                "\t\t\t  on mt.reservation_id = pr.id \n" +
                "\t\t\t  and pr.plant_id = pii.id \n" +
                "\t\t\t where mt.start_date >= sysdate \n" +
                "\t\t\t and mt.end_date <= (?2 -7 )) > 0)))\n" +
                "group by pie.name")
                .setParameter(1, plantName)
                .setParameter(2, startDate)
                .setParameter(3, endDate)
                .getResultList();

        List<Pair<String, Long>> l = new ArrayList<Pair<String, Long>>();

        for (Object[] obj : output) {

            String name = (String)obj[0];
            BigInteger bg = (BigInteger)obj[1];
            Long count = bg.longValue();
            Pair<String, Long> pair = new Pair<>(name, count);
            l.add(pair);
        }

        return l;
    }


    public List<String> query4() {
        List<String> output = em.createNativeQuery("select serial_number \n" +
                "from PLANT_INVENTORY_ITEM pii \n" +
                "\tleft join PLANT_RESERVATION pr \n" +
                "\t\ton pr.Plant_id = pii.id \n" +
                "\tleft join MAINTENANCE_TASK mt \n" +
                "\t\ton mt.reservation_id = pr.id \n" +
                "\t\tand mt.start_date >= (sysdate - 365)\n" +
                "group by serial_number \n" +
                "order by count(mt.id) desc , \n" +
                "\t\t sum(nvl(mt.total,0)) desc , \n" +
                "\t\t pii.serial_number desc \n" +
                "LIMIT 3")
                .getResultList();

        return output;
    }



}