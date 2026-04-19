package com.university;

import java.util.*;
import java.time.LocalDateTime;

// ========== БАЗОВЫЙ КЛАСС ПОЛЬЗОВАТЕЛЬ ==========
abstract class User {
    protected UUID id;
    protected String name;
    protected String email;
    protected String phone;

    public User(String name, String email, String phone) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public abstract void register();
    public abstract void login();

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}

// ========== СТУДЕНТ (наследуется от User) ==========
class Student extends User {
    private String studentId;      // Номер зачетки
    private List<Course> courses;
    private List<Grade> grades;
    private Scholarship scholarship;

    public Student(String name, String email, String phone, String studentId) {
        super(name, email, phone);
        this.studentId = studentId;
        this.courses = new ArrayList<>();
        this.grades = new ArrayList<>();
    }

    @Override
    public void register() {
        System.out.println("[STUDENT] Студент " + name + " зарегистрирован в системе");
    }

    @Override
    public void login() {
        System.out.println("[STUDENT] Студент " + name + " вошел в систему");
    }

    public void enrollInCourse(Course course) {
        if (!courses.contains(course)) {
            courses.add(course);
            course.addStudent(this);
            System.out.println("[STUDENT] " + name + " зарегистрирован на курс " + course.getName());
        }
    }

    public void viewGrades() {
        System.out.println("\n=== ОЦЕНКИ СТУДЕНТА " + name + " ===");
        for (Grade grade : grades) {
            System.out.println("Курс: " + grade.getExam().getCourse().getName() +
                    " | Оценка: " + grade.getScore() + " | Буква: " + grade.getLetterGrade());
        }
    }

    public void addGrade(Grade grade) {
        grades.add(grade);
    }

    public void setScholarship(Scholarship scholarship) {
        this.scholarship = scholarship;
    }

    public String getStudentId() { return studentId; }
    public List<Course> getCourses() { return courses; }
}

// ========== ПРЕПОДАВАТЕЛЬ (наследуется от User) ==========
class Teacher extends User {
    private String position;       // Должность
    private Department department;
    private List<Course> courses;

    public Teacher(String name, String email, String phone, String position) {
        super(name, email, phone);
        this.position = position;
        this.courses = new ArrayList<>();
    }

    @Override
    public void register() {
        System.out.println("[TEACHER] Преподаватель " + name + " зарегистрирован");
    }

    @Override
    public void login() {
        System.out.println("[TEACHER] Преподаватель " + name + " вошел в систему");
    }

    public void conductLesson(Course course, Schedule schedule) {
        System.out.println("[TEACHER] " + name + " провел занятие по курсу " + course.getName() +
                " в " + schedule.getTime() + ", аудитория " + schedule.getRoom());
    }

    public void assignGrade(Student student, Exam exam, int score) {
        Grade grade = new Grade(student, exam, score);
        student.addGrade(grade);
        exam.addGrade(grade);
        System.out.println("[TEACHER] Преподаватель " + name + " выставил оценку " + score +
                " студенту " + student.getName() + " по экзамену " + exam.getCourse().getName());
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

    public String getPosition() { return position; }
    public List<Course> getCourses() { return courses; }
    public void setDepartment(Department department) { this.department = department; }
    public Department getDepartment() { return department; }
}

// ========== АДМИНИСТРАТОР ==========
class Administrator {
    private UUID id;
    private String name;
    private List<String> actionLog;

    public Administrator(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.actionLog = new ArrayList<>();
    }

    public void manageFaculty(Faculty faculty, String action) {
        String log = LocalDateTime.now() + " - АДМИН " + name + " " + action + " факультет " + faculty.getName();
        actionLog.add(log);
        System.out.println("[ADMIN] " + action + " факультет " + faculty.getName());
    }

    public void manageDepartment(Department department, String action) {
        String log = LocalDateTime.now() + " - АДМИН " + name + " " + action + " кафедру " + department.getName();
        actionLog.add(log);
        System.out.println("[ADMIN] " + action + " кафедру " + department.getName());
    }

    public void manageCourse(Course course, String action) {
        System.out.println("[ADMIN] " + action + " курс " + course.getName());
    }

    public void manageUser(User user, String action) {
        System.out.println("[ADMIN] " + action + " пользователя " + user.getName());
    }

    public void printActionLog() {
        System.out.println("\n=== ЖУРНАЛ ДЕЙСТВИЙ АДМИНИСТРАТОРА ===");
        actionLog.forEach(System.out::println);
    }
}

// ========== УНИВЕРСИТЕТ (КОМПОЗИЦИЯ с Факультетом) ==========
class University {
    private String name;
    private String address;
    private List<Faculty> faculties;

    public University(String name, String address) {
        this.name = name;
        this.address = address;
        this.faculties = new ArrayList<>();
    }

    public void addFaculty(Faculty faculty) {
        faculties.add(faculty);
        System.out.println("[UNIVERSITY] Добавлен факультет " + faculty.getName());
    }

    public void removeFaculty(Faculty faculty) {
        faculties.remove(faculty);
        System.out.println("[UNIVERSITY] Удален факультет " + faculty.getName());
    }

    public String getName() { return name; }
    public List<Faculty> getFaculties() { return faculties; }
}

// ========== ФАКУЛЬТЕТ (АГРЕГАЦИЯ с Кафедрой) ==========
class Faculty {
    private String name;
    private List<Department> departments;

    public Faculty(String name) {
        this.name = name;
        this.departments = new ArrayList<>();
    }

    public void addDepartment(Department department) {
        departments.add(department);
        department.setFaculty(this);
        System.out.println("[FACULTY] Добавлена кафедра " + department.getName() + " на факультет " + name);
    }

    public void removeDepartment(Department department) {
        departments.remove(department);
        System.out.println("[FACULTY] Удалена кафедра " + department.getName());
    }

    public String getName() { return name; }
    public List<Department> getDepartments() { return departments; }
}

// ========== КАФЕДРА (АГРЕГАЦИЯ с Преподавателями и Курсами) ==========
class Department {
    private String name;
    private Faculty faculty;
    private List<Teacher> teachers;
    private List<Course> courses;

    public Department(String name) {
        this.name = name;
        this.teachers = new ArrayList<>();
        this.courses = new ArrayList<>();
    }

    public void addTeacher(Teacher teacher) {
        teachers.add(teacher);
        teacher.setDepartment(this);
        System.out.println("[DEPARTMENT] Добавлен преподаватель " + teacher.getName() + " на кафедру " + name);
    }

    public void removeTeacher(Teacher teacher) {
        teachers.remove(teacher);
        System.out.println("[DEPARTMENT] Удален преподаватель " + teacher.getName());
    }

    public void addCourse(Course course) {
        courses.add(course);
        course.setDepartment(this);
        System.out.println("[DEPARTMENT] Добавлен курс " + course.getName() + " на кафедру " + name);
    }

    public void setFaculty(Faculty faculty) { this.faculty = faculty; }
    public String getName() { return name; }
    public List<Teacher> getTeachers() { return teachers; }
    public List<Course> getCourses() { return courses; }
}

// ========== КУРС ==========
class Course {
    private String name;
    private String description;
    private Department department;
    private List<Teacher> teachers;
    private List<Student> students;
    private Schedule schedule;
    private List<Exam> exams;

    public Course(String name, String description) {
        this.name = name;
        this.description = description;
        this.teachers = new ArrayList<>();
        this.students = new ArrayList<>();
        this.exams = new ArrayList<>();
    }

    public void addTeacher(Teacher teacher) {
        if (!teachers.contains(teacher)) {
            teachers.add(teacher);
            teacher.addCourse(this);
        }
    }

    public void addStudent(Student student) {
        if (!students.contains(student)) {
            students.add(student);
        }
    }

    public void removeStudent(Student student) {
        students.remove(student);
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public void addExam(Exam exam) {
        exams.add(exam);
    }

    public void setDepartment(Department department) { this.department = department; }
    public String getName() { return name; }
    public List<Student> getStudents() { return students; }
    public List<Teacher> getTeachers() { return teachers; }
    public Schedule getSchedule() { return schedule; }
    public List<Exam> getExams() { return exams; }
}

// ========== РАСПИСАНИЕ ==========
class Schedule {
    private LocalDateTime dateTime;
    private String room;
    private Course course;
    private Teacher teacher;

    public Schedule(LocalDateTime dateTime, String room, Course course, Teacher teacher) {
        this.dateTime = dateTime;
        this.room = room;
        this.course = course;
        this.teacher = teacher;
    }

    public void createSchedule() {
        System.out.println("[SCHEDULE] Создано расписание для курса " + course.getName() +
                ": " + dateTime + ", ауд. " + room);
    }

    public void updateSchedule(LocalDateTime newDateTime, String newRoom) {
        this.dateTime = newDateTime;
        this.room = newRoom;
        System.out.println("[SCHEDULE] Расписание обновлено: " + dateTime + ", ауд. " + room);
    }

    public LocalDateTime getTime() { return dateTime; }
    public String getRoom() { return room; }
}

// ========== ЭКЗАМЕН ==========
class Exam {
    private UUID id;
    private Course course;
    private LocalDateTime date;
    private List<Grade> grades;

    public Exam(Course course, LocalDateTime date) {
        this.id = UUID.randomUUID();
        this.course = course;
        this.date = date;
        this.grades = new ArrayList<>();
        course.addExam(this);
    }

    public void scheduleExam() {
        System.out.println("[EXAM] Назначен экзамен по курсу " + course.getName() + " на " + date);
    }

    public void addGrade(Grade grade) {
        grades.add(grade);
    }

    public Course getCourse() { return course; }
    public LocalDateTime getDate() { return date; }
    public List<Grade> getGrades() { return grades; }
}

// ========== ОЦЕНКА ==========
class Grade {
    private Student student;
    private Exam exam;
    private int score;
    private String letterGrade;

    public Grade(Student student, Exam exam, int score) {
        this.student = student;
        this.exam = exam;
        this.score = score;
        this.letterGrade = calculateLetterGrade(score);
    }

    private String calculateLetterGrade(int score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    public Student getStudent() { return student; }
    public Exam getExam() { return exam; }
    public int getScore() { return score; }
    public String getLetterGrade() { return letterGrade; }
}

// ========== ДОПОЛНИТЕЛЬНЫЕ СУЩНОСТИ ==========

// Стипендия
class Scholarship {
    private String type;
    private double amount;
    private Student student;
    private LocalDateTime awardedDate;

    public Scholarship(String type, double amount, Student student) {
        this.type = type;
        this.amount = amount;
        this.student = student;
        this.awardedDate = LocalDateTime.now();
        student.setScholarship(this);
    }

    public void award() {
        System.out.println("[SCHOLARSHIP] Студенту " + student.getName() +
                " назначена стипендия " + type + " в размере " + amount);
    }
}

// Научный проект
class ResearchProject {
    private String name;
    private String description;
    private List<Teacher> researchers;
    private List<Student> participants;

    public ResearchProject(String name, String description) {
        this.name = name;
        this.description = description;
        this.researchers = new ArrayList<>();
        this.participants = new ArrayList<>();
    }

    public void addResearcher(Teacher teacher) {
        researchers.add(teacher);
    }

    public void addParticipant(Student student) {
        participants.add(student);
    }

    public void startProject() {
        System.out.println("[RESEARCH] Научный проект " + name + " запущен");
    }
}

// Журнал успеваемости
class Gradebook {
    private String name;
    private Course course;
    private Map<Student, List<Grade>> studentGrades;

    public Gradebook(String name, Course course) {
        this.name = name;
        this.course = course;
        this.studentGrades = new HashMap<>();
    }

    public void addGrade(Student student, Grade grade) {
        studentGrades.computeIfAbsent(student, k -> new ArrayList<>()).add(grade);
    }

    public void printGradebook() {
        System.out.println("\n=== ЖУРНАЛ УСПЕВАЕМОСТИ: " + name + " ===");
        for (Map.Entry<Student, List<Grade>> entry : studentGrades.entrySet()) {
            System.out.print(entry.getKey().getName() + ": ");
            for (Grade g : entry.getValue()) {
                System.out.print(g.getLetterGrade() + " ");
            }
            System.out.println();
        }
    }
}

// ========== ГЛАВНЫЙ КЛАСС ==========
public class Main {
    public static void main(String[] args) {
        System.out.println("=== УНИВЕРСИТЕТСКАЯ СИСТЕМА ===\n");

        // Создание университета
        University uni = new University("КазНУ им. Аль-Фараби", "пр. Аль-Фараби, 71");

        // Создание факультетов
        Faculty fit = new Faculty("Факультет информационных технологий");
        Faculty fmm = new Faculty("Факультет математики и механики");
        uni.addFaculty(fit);
        uni.addFaculty(fmm);

        // Создание кафедр
        Department cs = new Department("Кафедра компьютерных наук");
        Department math = new Department("Кафедра математики");
        fit.addDepartment(cs);
        fmm.addDepartment(math);

        // Создание преподавателей
        Teacher teacher1 = new Teacher("Аскаров Ерлан", "erlan@university.kz", "+77011234567", "Профессор");
        Teacher teacher2 = new Teacher("Сагиндыкова Айгуль", "aigul@university.kz", "+77017654321", "Доцент");
        cs.addTeacher(teacher1);
        math.addTeacher(teacher2);

        // Регистрация преподавателей
        teacher1.register();
        teacher2.register();

        // Создание курсов
        Course javaCourse = new Course("Java Programming", "Основы программирования на Java");
        Course algorithms = new Course("Algorithms", "Структуры данных и алгоритмы");
        cs.addCourse(javaCourse);
        cs.addCourse(algorithms);
        javaCourse.addTeacher(teacher1);
        algorithms.addTeacher(teacher1);

        // Создание студентов
        Student student1 = new Student("Бекжан Алиев", "bekzhan@student.kz", "+77009998877", "20BD01001");
        Student student2 = new Student("Мадина Оразова", "madina@student.kz", "+77008887766", "20BD01002");
        student1.register();
        student2.register();

        // Регистрация на курсы
        student1.enrollInCourse(javaCourse);
        student1.enrollInCourse(algorithms);
        student2.enrollInCourse(javaCourse);

        // Создание расписания
        Schedule schedule1 = new Schedule(LocalDateTime.now().plusDays(2), "301", javaCourse, teacher1);
        schedule1.createSchedule();

        // Создание экзамена
        Exam exam1 = new Exam(javaCourse, LocalDateTime.now().plusMonths(1));
        exam1.scheduleExam();

        // Выставление оценок
        teacher1.assignGrade(student1, exam1, 92);
        teacher1.assignGrade(student2, exam1, 78);

        // Просмотр оценок студентами
        student1.viewGrades();
        student2.viewGrades();

        // Создание администратора (ИСПРАВЛЕНО: Administrator вместо Admin)
        Administrator admin = new Administrator("Абдрахманова Алия");
        admin.manageFaculty(fit, "обновил");

        // Дополнительные сущности
        Scholarship scholarship = new Scholarship("Академическая", 85000, student1);
        scholarship.award();

        ResearchProject project = new ResearchProject("AI in Education", "Искусственный интеллект в образовании");
        project.addResearcher(teacher1);
        project.addParticipant(student1);
        project.startProject();

        Gradebook gradebook = new Gradebook("Java Programming Grades", javaCourse);
        gradebook.printGradebook();

        // Вывод журнала действий администратора
        admin.printActionLog();
    }
}