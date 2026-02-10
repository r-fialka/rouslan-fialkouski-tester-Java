package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

public class DataBasePrepareService {

    DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    public void clearDataBaseEntries(){
        Connection connection = null;
        try{
            connection = dataBaseTestConfig.getConnection();

            //set parking entries to available
            connection.prepareStatement("update parking set available = true").execute();

            //clear ticket entries;
            connection.prepareStatement("truncate table ticket").execute();

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }


    public void updateInTimeForVehicle(String vehicleRegNumber, Date newInTime) {
        try (Connection con = dataBaseTestConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE ticket SET IN_TIME = ? WHERE VEHICLE_REG_NUMBER = ?"
             )) {

            ps.setTimestamp(1, new Timestamp(newInTime.getTime()));
            ps.setString(2, vehicleRegNumber);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
