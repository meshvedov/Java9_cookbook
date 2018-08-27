package ch15_testing.utils;

import ch15_testing.api.SpeedModel;
import ch15_testing.api.TrafficUnit;
import ch15_testing.api.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class DbUtil {



    public static boolean isEnoughData(int trafficUnitsNumber) {
        try (Connection conn = getDbConnection()) {
            PreparedStatement st = conn.prepareStatement("select count(*) from data");
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count >= trafficUnitsNumber;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Connection getDbConnection() {
        String url = "jdbc:postgresql://localhost:5432/java9cookbook";
        Properties prop = new Properties();
        prop.put("user", "mic");
        prop.put("password", "123456");

        try {
            return DriverManager.getConnection(url, prop);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<TrafficUnit> selectData(int trafficUnitsNumber) {
        List<TrafficUnit> result = new ArrayList<>();
        try (Connection conn = getDbConnection();
             PreparedStatement st = conn.prepareStatement("select vhicle_type, horse_power, weight_pounds,passengers_count,payload_pounds, speed_limit_mph, " +
                     "temperature,road_condition,tire_condition,traction,speed from data order by id limit " + trafficUnitsNumber)) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                result.add(new TrafficUnitImpl(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void recordDataCommon(int trafficUnitsNumber, double timeSec, String dateLocation, double[] speedLimitByLane) {
        String limits = Arrays.stream(speedLimitByLane).mapToObj(Double::toString).collect(Collectors.joining(", "));
        String sql = "insert into data_common(traffic_units_number, time_sec, date_location, speed_limit_by_lane) values(?,?,?,?)";
        try (Connection conn = getDbConnection();
            PreparedStatement st = conn.prepareStatement(sql)){
            int i = 1;
            st.setInt(i++, trafficUnitsNumber);
            st.setDouble(i++, timeSec);
            st.setString(i++, dateLocation);
            st.setString(i++, limits);
            int count = st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void storeResult(String process, int trafficUnitsNumber, double timeSec, String dateLocation, double[] speedLimitByLane, String result) {
        String limits = Arrays.stream(speedLimitByLane).mapToObj(Double::toString).collect(Collectors.joining(", "));
        String sql = "insert into result(process, traffic_units_number, time_sec, date_location, speed_limit_by_lane, result) values (?,?,?,?,?,?)";
        try (Connection conn = getDbConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            int i = 1;
            st.setString(i++, process);
            st.setInt(i++, trafficUnitsNumber);
            st.setDouble(i++, timeSec);
            st.setString(i++, dateLocation);
            st.setString(i++, limits);
            st.setString(i++, result);
            int count = st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void recordData(Connection conn, TrafficUnit trafficUnit, double speed) {
        String sql = "insert into data(vehicle_type, horse_power, wheight_pounds, passenger_count, payload_pounds, speed_limit_mph, " +
                "temperature, road_condition, tire_condition, traction, speed) values (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            int i=1;
            st.setString(i++, trafficUnit.getVehicleType().name());
            st.setInt(i++, trafficUnit.getHorsePower());
            st.setInt(i++, trafficUnit.getWeightPounds());
            st.setInt(i++, trafficUnit.getPassengersCount());
            st.setInt(i++, trafficUnit.getPayloadPounds());
            st.setDouble(i++, trafficUnit.getSpeedLimitMph());
            st.setInt(i++, trafficUnit.getTemperature());
            st.setString(i++, trafficUnit.getRoadCondition().name());
            st.setString(i++, trafficUnit.getTireCondition().name());
            st.setDouble(i++, trafficUnit.getTraction());
            st.setDouble(i++, speed);
            int count = st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createResultTable() {
        execute("drop table if exists result");
        execute("create table result(id serial not null," +
                "process character varying not null, " +
                "traffic_units_number integer not null," +
                "time_sec numeric not null," +
                "date_location character varying not null," +
                "speed_limit_by_lane character varying not null," +
                "result character varying not null);");
    }

    public static void createDataTable() {
        execute("drop table if exists data_common");
        execute("create table data_common(" +
                "traffic_units_number integer not null," +
                "time_sec numeric not null," +
                "date_location character varying not null," +
                "speed_limit_by_lane character varying not null);");
        execute("drop table if exists data");
        execute("create table data(" +
                "id serial not null," +
                "vehicle_type character varying not null," +
                "horse_power integer not null," +
                "weight_pounds integer not null," +
                "passengers_count integer not null," +
                "payload_pounds integer not null," +
                "speed_limit_mph numeric not null," +
                "temperature integer not null," +
                "road_condition character varying not null," +
                "tire_condition character varying not null," +
                "traction numeric not null," +
                "speed numeric not null);");
    }

    public static void execute(String sql) {
        try (Connection conn = getDbConnection();
        PreparedStatement st = conn.prepareStatement(sql)) {
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String selectResult(String process) {
        try (Connection conn = getDbConnection();
        PreparedStatement st = conn.prepareStatement("select result from result where process = ?")) {
            st.setString(1, process);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static class TrafficUnitImpl implements TrafficUnit {
        private int horsePower, weightPounds, payloadPounds, passengersCount, temperature;
        private Vehicle.VehicleType vehicleType;
        private double speedLimitMph, traction, speed;
        private SpeedModel.RoadCondition roadCondition;
        private SpeedModel.TireCondition tireCondition;

        public TrafficUnitImpl(ResultSet rs) throws SQLException {
            int i = 1;
            this.vehicleType = Vehicle.VehicleType.valueOf(rs.getString(i++));
            this.horsePower = rs.getInt(i++);
            this.weightPounds = rs.getInt(i++);
            this.passengersCount = rs.getInt(i++);
            this.payloadPounds = rs.getInt(i++);
            this.speedLimitMph = rs.getDouble(i++);
            this.temperature = rs.getInt(i++);
            this.roadCondition = SpeedModel.RoadCondition.valueOf(rs.getString(i++));
            this.tireCondition = SpeedModel.TireCondition.valueOf(rs.getString(i++));
            this.traction = rs.getDouble(i++);
            this.speed = rs.getDouble(i++);
        }

        @Override
        public int getHorsePower() {
            return horsePower;
        }

        @Override
        public int getWeightPounds() {
            return weightPounds;
        }

        @Override
        public int getPayloadPounds() {
            return payloadPounds;
        }

        public int getPassengersCount() {
            return passengersCount;
        }

        @Override
        public int getTemperature() {
            return temperature;
        }

        @Override
        public Vehicle.VehicleType getVehicleType() {
            return vehicleType;
        }

        @Override
        public double getSpeedLimitMph() {
            return speedLimitMph;
        }

        @Override
        public double getTraction() {
            return traction;
        }

        @Override
        public double getSpeed() {
            return speed;
        }

        @Override
        public SpeedModel.RoadCondition getRoadCondition() {
            return roadCondition;
        }

        @Override
        public SpeedModel.TireCondition getTireCondition() {
            return tireCondition;
        }
    }
}
