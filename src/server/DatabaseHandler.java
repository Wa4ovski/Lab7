package server;

import common.Parsers;
import common.exceptions.*;
import common.model.*;
import common.model.Worker;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;

public class DatabaseHandler {

    private static final String pepper = "2Hq@*!8fdAQl";
    private static final String ADD_USER_REQUEST = "INSERT INTO USERS (username, password) VALUES (?, ?)";
    private static final String VALIDATE_USER_REQUEST = "SELECT COUNT(*) AS count FROM USERS WHERE username = ? AND password = ?";
    private static final String FIND_USERNAME_REQUEST = "SELECT COUNT(*) AS count FROM USERS WHERE username = ?";
    private static final String ADD_WORKER_REQUEST = "INSERT INTO WORKERS (name, x_coord, y_coord, salary, start_date, " +
            "end_date, status, birthday, height, passport, owner, id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";//+
    private static final String LOAD_WORKERS_REQUEST = "SELECT * FROM WORKERS";//+
    private static final String GET_MAX_WORKER_ID_REQUEST = "SELECT MAX(id) FROM workers";//"SELECT last_value AS id FROM Worker_id_seq";//?
    private static final String CHECK_ID_PRESENT_REQUEST = "SELECT COUNT(*) AS count FROM WORKERS WHERE id = ?";//+
    private static final String REMOVE_BY_WORKER_ID_REQUEST = "DELETE FROM WORKERS WHERE id = ?";//+
    private static final String TICKETS_BY_OWNER_REQUEST = "SELECT (*) FROM TICKETS WHERE owner = ?";
    private static final String IS_OWNER_REQUEST = "SELECT COUNT(*) FROM WORKERS WHERE id = ? AND owner = ?";//+
    private static final String UPDATE_NAME_BY_ID_REQUEST = "UPDATE WORKERS SET name = ? WHERE id = ?";//+
    private static final String UPDATE_SALARY_BY_ID_REQUEST = "UPDATE WORKERS SET salary = ? WHERE id = ?";//+
    private static final String UPDATE_COORD_BY_ID_REQUEST = "UPDATE WORKERS SET x_coord = ?, y_coord = ? WHERE id = ?";//+
    private static final String UPDATE_START_DATE_BY_ID_REQUEST = "UPDATE WORKERS SET start_date = ? WHERE id = ?"; //+
    private static final String UPDATE_END_DATE_BY_ID_REQUEST = "UPDATE WORKERS SET end_date = ? WHERE id = ?"; //+
        private static final String UPDATE_STATUS_BY_ID_REQUEST = "UPDATE WORKERS SET status = ? WHERE id = ?"; //+
    private static final String UPDATE_PERSON_BY_ID_REQUEST = "UPDATE WORKERS SET " +
            "birthday = ?, " +
            "height = ?, " +
            "passport = ?, " +
            "WHERE id = ?"; //+


    private String URL;
    private String username;
    private String password;
    private Connection connection;


    public DatabaseHandler(String URL, String username, String password) {
        this.URL = URL;
        this.username = username;
        this.password = password;
    }

    public void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(URL, username, password);
            System.out.println("Подключение к базе данных установлено.");
        } catch (SQLException e) {
            System.err.println("Не удалось выполнить подключение к базе данных. Завершение работы.");
            System.exit(-1);
        }
    }

    public LinkedHashSet<Worker> loadCollectionFromDB() {
        LinkedHashSet<Worker> collection = new LinkedHashSet<>();
        try {
            PreparedStatement joinStatement = connection.prepareStatement(LOAD_WORKERS_REQUEST);
            ResultSet result = joinStatement.executeQuery();

            while (result.next()) {
                try {
                    Worker t = extractWorkerFromResult(result);
                    collection.add(t);
                    Worker.addToIdMap(t);
                } catch (InvalidDBOutputException e) {
                    System.out.println("Неверный объект");
                    continue;
                } catch (DomainViolationException ex){
                    System.out.println("Неверный объект даты");
                    continue;
                }
            }

            joinStatement.close();
            System.out.println("Коллекция успешно загружена из базы данных. Количество элементов: " + collection.size());
        } catch (SQLException e) {
            System.out.println("Произошла ошибка при загрузке коллекции из базы данных. Завершение работы.");
            e.printStackTrace();
            System.exit(-1);
        }
        return collection;
    }

    private Worker extractWorkerFromResult(ResultSet result) throws SQLException, InvalidDBOutputException, DomainViolationException {
        long workerId = result.getInt("id");
        if (workerId < 1) throw new InvalidDBOutputException();
        String workerName = result.getString("name");
        if (workerName == null || workerName.isEmpty()) throw new InvalidDBOutputException();
        double x = result.getDouble("x_coord");
        float y = result.getFloat("y_coord");
        long salary = result.getLong("salary"); //!
       // LocalDate creationDate = result.getDate("creation_date").toLocalDate();
        LocalDateTime startDate = Parsers.parseTheLocalDateTime(result.getString("start_date"));//!
        java.util.Date endDate = Parsers.parseTheDate(result.getString("end_date"));
        Status status = Status.valueOf(result.getString("status"));//!

       // long venueId = result.getLong("venueid");
        //if (venueId < 1) throw new InvalidDBOutputException();
        LocalDateTime birthday = Parsers.parseTheLocalDateTime(result.getString("birthday"));//!
        int height = result.getInt("height");//!
        String passportID = result.getString("passport");


        Coordinates coordinates = new Coordinates(x, y);
        Person person = new Person(birthday, height, passportID);
        Worker worker = new Worker(workerId, workerName, coordinates, salary, startDate, endDate, status, person);

        return worker;
    }

    public boolean insertWorker(Worker t, String owner) {
        String name = t.getName();
        Coordinates coordinates = t.getCoordinates();
        long salary = t.getSalary();
        LocalDateTime startDate = t.getStartDate();
        Date endDate = t.getEndDate();
        Status status = t.getStatus();
       Person person = t.getPerson();
       Long id = t.getId();

        try {
            connection.setAutoCommit(false);
            connection.setSavepoint();

            PreparedStatement addToWorkersStatement = connection.prepareStatement(ADD_WORKER_REQUEST);
            addToWorkersStatement.setString(1, name);
            addToWorkersStatement.setDouble(2, coordinates.getX());
            addToWorkersStatement.setFloat(3, coordinates.getY());
            addToWorkersStatement.setLong(4, salary);
            addToWorkersStatement.setString(5, FileManager.localDateTimeToString(startDate));
            addToWorkersStatement.setString(6, FileManager.dateToString(endDate));
            addToWorkersStatement.setString(7, status.toString());
            addToWorkersStatement.setString(8, FileManager.localDateTimeToString(person.getBirthday()));
            addToWorkersStatement.setInt(9, person.getHeight());
            addToWorkersStatement.setString(10, person.getPassportID());
            addToWorkersStatement.setString(11, owner);
            addToWorkersStatement.setLong(12, id);
            addToWorkersStatement.executeUpdate();
            addToWorkersStatement.close();

            connection.commit();
            connection.setAutoCommit(true);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            rollback();
        }

        return false;
    }

    public void updateWorkerName(String name, long id) throws SQLException{
        PreparedStatement updateName = connection.prepareStatement(UPDATE_NAME_BY_ID_REQUEST);
        updateName.setString(1, name);
        updateName.setLong(2, id);
        updateName.executeUpdate();
        updateName.close();
    }

    public void updateWorkerSalary(long salary, long id) throws SQLException {
        PreparedStatement updateSalary = connection.prepareStatement(UPDATE_SALARY_BY_ID_REQUEST);
        updateSalary.setLong(1, salary);
        updateSalary.setLong(2, id);
        updateSalary.executeUpdate();
        updateSalary.close();
    }

    public void updateWorkerCoordinates(Coordinates coords, long id) throws SQLException {
        PreparedStatement updateCoordinates = connection.prepareStatement(UPDATE_COORD_BY_ID_REQUEST);
        updateCoordinates.setDouble(1, coords.getX());
        updateCoordinates.setFloat(2, coords.getY());
        updateCoordinates.setLong(3, id);
        updateCoordinates.executeUpdate();
        updateCoordinates.close();
    }

    public void updateWorkerStartDate(LocalDateTime startDate, long id) throws SQLException {
        PreparedStatement updateStartDate = connection.prepareStatement(UPDATE_START_DATE_BY_ID_REQUEST);
        updateStartDate.setString(1, FileManager.localDateTimeToString(startDate));
        updateStartDate.setLong(2, id);
        updateStartDate.executeUpdate();
        updateStartDate.close();
    }

    public void updateWorkerEndDate(Date endDate, long id) throws SQLException {
        PreparedStatement updateEndDate = connection.prepareStatement(UPDATE_END_DATE_BY_ID_REQUEST);
        updateEndDate.setString(1, FileManager.dateToString(endDate));
        updateEndDate.setLong(2, id);
        updateEndDate.executeUpdate();
        updateEndDate.close();
    }

    public void updateWorkerStatus(Status status, long id) throws SQLException {
        PreparedStatement updateStatus = connection.prepareStatement(UPDATE_STATUS_BY_ID_REQUEST);
        updateStatus.setString(1, status.name());
        updateStatus.setLong(2, id);
        updateStatus.executeUpdate();
        updateStatus.close();
    }

    public void updateWorkerPerson(Person person, long id) throws SQLException {
        PreparedStatement updatePerson = connection.prepareStatement(UPDATE_PERSON_BY_ID_REQUEST);
        updatePerson.setString(1, FileManager.localDateTimeToString(person.getBirthday()));
        updatePerson.setInt(2, person.getHeight());
        updatePerson.setString(3, person.getPassportID());
        updatePerson.executeUpdate();
        updatePerson.close();
    }

    public boolean removeWorkerByID(long id, String initiator) throws SQLException, InsufficientPermissionException {
        if (!checkIdExistence(id)) return false;
        if (!isOwnerOf(id, initiator)) throw new InsufficientPermissionException();
        PreparedStatement removeWorkerStatement = connection.prepareStatement(REMOVE_BY_WORKER_ID_REQUEST);
        connection.setAutoCommit(false);
        connection.setSavepoint();
        removeWorkerStatement.setLong(1, id);
        removeWorkerStatement.executeUpdate();
        removeWorkerStatement.close();
        connection.commit();
        connection.setAutoCommit(true);
        return true;
    }

    public boolean checkIdExistence(long id) throws SQLException {
        PreparedStatement checkId = connection.prepareStatement(CHECK_ID_PRESENT_REQUEST);
        checkId.setLong(1, id);
        ResultSet resultSet = checkId.executeQuery();
        resultSet.next();
        if (resultSet.getInt(1) == 0) return false;
        else return true;
    }
    public long getNextWorkerId() {
        try {
            PreparedStatement getMaxId = connection.prepareStatement(GET_MAX_WORKER_ID_REQUEST);
            ResultSet result = getMaxId.executeQuery();
            if (result.next()) {
                //System.out.println(result.getLong("id"));
                return result.getLong(1);
            }
            else {
                System.out.println("F");
                return 0;
            }
        } catch (SQLException e) {
            System.out.println("Ошибка генерации id");
        }
        return 0;
    }



    public boolean registerUser(String username, String password) throws SQLException {
        if (userExists(username)) return false;
        PreparedStatement addStatement = connection.prepareStatement(ADD_USER_REQUEST);
        addStatement.setString(1, username);
        addStatement.setString(2, DataHasher.encryptStringMD2(password + pepper));
        addStatement.executeUpdate();
        addStatement.close();
        return true;
    }

    public boolean validateUser(String username, String password) throws SQLException {
        PreparedStatement findUserStatement = connection.prepareStatement(VALIDATE_USER_REQUEST);
        String hashedPassword = DataHasher.encryptStringMD2(password + pepper);
        findUserStatement.setString(1, username);
        findUserStatement.setString(2, hashedPassword);
        ResultSet result = findUserStatement.executeQuery();
        result.next();
        int count = result.getInt(1);
        findUserStatement.close();
        if (count == 1) return true;
        return false;
    }

    public boolean userExists(String username) throws SQLException {
        PreparedStatement findStatement = connection.prepareStatement(FIND_USERNAME_REQUEST);
        findStatement.setString(1, username);
        ResultSet result = findStatement.executeQuery();
        result.next();
        int count = result.getInt(1);
        findStatement.close();
        if (count == 1) return true;
        return false;
    }

    public boolean isOwnerOf(long id, String username) throws SQLException {
        PreparedStatement ownerStatement = connection.prepareStatement(IS_OWNER_REQUEST);
        ownerStatement.setLong(1, id);
        ownerStatement.setString(2, username);
        ResultSet result = ownerStatement.executeQuery();
        result.next();
        if (result.getInt(1) == 1) return true;
        return false;
    }

    public void rollback() {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("Не удалось откатить изменения.");
        }
    }
}