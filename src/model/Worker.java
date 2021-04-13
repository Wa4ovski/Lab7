package model;

//import com.xml.ZonedDateTimeXmlAdapter;

import util.CommandProcessor;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


/**
 * Worker data class
 */

public class Worker implements Comparable<Worker> {
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private long salary; //Значение поля должно быть больше 0
    private LocalDateTime startDate; //Поле не может быть null
    private Date endDate; //Поле может быть null
    private Status status; //Поле не может быть null
    public Person person; //Поле не может быть null

    /**
     //* @param id - worker's ID
     * @param name - worker's name
     * @param coordinates - worker's coordinates object
     //* @param creationDate - LocalDateTime object of creation date
     * @param salary - worker's salary
     * @param startDate -
     * @param endDate -
     * @param status - status of the worker
     * @param person - personal data of worker
     */
    public Worker(String name, Coordinates coordinates, long salary, LocalDateTime startDate, Date endDate, Status status, Person person) {
        this.name = name;
        this.coordinates = coordinates;
        this.salary = salary;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.person = person;
    }

    public Worker() {
    }


    private static long nextId;

    private static HashMap<Long, Worker> ticketIdMap;

    static {
        nextId = 1;
        ticketIdMap = new HashMap<>();
    }

    {
        this.id = nextId;
        nextId += 1;
        creationDate = LocalDateTime.now();
        coordinates = new Coordinates();
        ticketIdMap.put(this.id, this);
    }

    public void setId(Long id){
        ticketIdMap.remove(this.id);
        this.id = id;
        ticketIdMap.put(this.id, this);
        //System.out.println(id);

    }

    public static void resetId() {
        nextId = 1;
    }

    public static Worker getWorkerById(long id) {
        return ticketIdMap.get(id);
    }

    public static void removeFromIdMap(long id) {
        ticketIdMap.remove(id);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Long getSalary() {
        return salary;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Status getStatus() {
        return status;
    }

    public Person getPerson() {
        return person;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Worker worker = (Worker) o;
        return Objects.equals(id, worker.id) ;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, salary, startDate, endDate, status, person);
    }


    @Override
    public String toString() {
        return "Worker{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates.toString() +
                ", creationDate=" + creationDate.toString() +
                ", salary=" + salary +
                ", startDate=" + startDate.toString()+
                ", endDate=" + CommandProcessor.DateToString(endDate) +
                ", status=" + status.toString() +
                ", person=" + person.toString() +
                '}';
    }

    @Override
    public int compareTo(Worker o) {
        return (int)(salary - o.salary);
    }
}
