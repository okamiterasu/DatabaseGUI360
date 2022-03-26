// import java.nio.file.Path;
// import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
// Temporary object. to be replaced with the real one later
class Prof{
	int id;
	String department;
	String firstName;
	String lastName;
	String birthDate;
	public Prof(int id, String department, String firstName, String lastName, String birthDate) {
		this.id = id;
		this.department = department;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
	}
}

public class Database
{
	private final Connection connection;
	private final PreparedStatement insertPerson;
	private final PreparedStatement getPerson;
	private final PreparedStatement updatePerson;
	private final PreparedStatement deletePerson;

	private final PreparedStatement getStudent;
	// private final PreparedStatement deleteStudent;

	private final PreparedStatement insertEmployee;
	private final PreparedStatement getEmployee;
	private final PreparedStatement updateEmployee;
	private final PreparedStatement deleteEmployee;

	private final PreparedStatement insertProfessor;
	private final PreparedStatement getProfessor;
	private final PreparedStatement deleteProfessor;

	private final PreparedStatement getTA;

	private final PreparedStatement getAdmin;
	public Database(String dbPath) throws SQLException
	{
		final var url = "jdbc:sqlite:" + dbPath;
		this.connection = DriverManager.getConnection(url);
		this.connection.setAutoCommit(false);
		//allows the setup of a form connection
		//this will also allow to present all the data on the form when searching 
		this.insertPerson = this.connection.prepareStatement("""
			INSERT INTO PERSON (id, first_name, last_name, birth_date)
				VALUES (?, ?, ?, ?)""");
		this.getPerson = this.connection.prepareStatement("""
				SELECT first_name, last_name, birth_date
					FROM PERSON
					WHERE id = ?""");
		this.updatePerson = this.connection.prepareStatement("""
			UPDATE PERSON 
				SET
					first_name = ?,
					last_name = ?,
					birth_date = ?
				WHERE id = ?""");
		this.deletePerson = this.connection.prepareStatement("""
			DELETE FROM PERSON
				WHERE id = ?""");
		
		this.getStudent = this.connection.prepareStatement("""
			SELECT first_name, last_name, birth_date
				FROM PERSON
					INNER JOIN STUDENT
						ON PERSON.id = STUDENT.id
				WHERE STUDENT.id = ?""");

		this.insertEmployee = this.connection.prepareStatement("""
			INSERT INTO EMPLOYEE (person_id, department)
				VALUES (?, ?)""");
		this.getEmployee = this.connection.prepareStatement("""
			SELECT first_name, last_name, birth_date, department
				FROM EMPLOYEE
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id
				WHERE id = ?""");
		this.updateEmployee = this.connection.prepareStatement("""
			UPDATE EMPLOYEE 
				SET department = ?
				WHERE person_id = ?""");
		this.deleteEmployee = this.connection.prepareStatement("""
			DELETE FROM EMPLOYEE
				WHERE person_id = ?""");

		this.insertProfessor = this.connection.prepareStatement("""
			INSERT INTO PROFESSOR (id)
				VALUES (?)""");
		this.getProfessor = this.connection.prepareStatement("""
			SELECT first_name, last_name, birth_date, department
				FROM PROFESSOR
					INNER JOIN EMPLOYEE
						ON PROFESSOR.id = EMPLOYEE.person_id
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id
				WHERE PROFESSOR.id = ?""");
		this.deleteProfessor = this.connection.prepareStatement("""
			DELETE FROM PROFESSOR
				WHERE id = ?""");

		this.getTA = this.connection.prepareStatement("""
			SELECT first_name, last_name, birth_date, department
				FROM TA
					INNER JOIN EMPLOYEE
						ON TA.id = EMPLOYEE.person_id
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id
				WHERE TA.id = ?""");
		this.getAdmin = this.connection.prepareStatement("""
			SELECT first_name, last_name, birth_date, department
				FROM ADMIN
					INNER JOIN EMPLOYEE
						ON ADMIN.employee_id = EMPLOYEE.person_id
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id
				WHERE ADMIN.employee_id = ?""");
	}
	
	/** 
	 * Checks to see if the given id is a valid account and determines its rights.
	 * Returns an array of flags that specify in order:
	 * 1. Account exists
	 * 2. Is student
	 * 3. Is employee
	 * 4. Is teacher
	 * 5. Is ta
	 * 6. Is admin
	 * 
	 * @param id
	 * @return boolean[5]
	 * @throws SQLException
	 */
	public boolean[] checkLogin(int id) throws SQLException
	{
		final var flags = new boolean[6];

		this.getPerson.setInt(1, id);
		final var getPersonRes = this.getPerson.executeQuery();
		flags[0] = getPersonRes.isBeforeFirst();
		getPersonRes.close();

		this.getStudent.setInt(1, id);
		final var getStudentRes = this.getStudent.executeQuery();
		flags[1] = getStudentRes.isBeforeFirst();
		getStudentRes.close();

		this.getEmployee.setInt(1, id);
		final var getEmployeeRes = this.getEmployee.executeQuery();
		flags[2] = getEmployeeRes.isBeforeFirst();
		getEmployeeRes.close();
		
		this.getProfessor.setInt(1, id);
		final var getProfessorRes = this.getProfessor.executeQuery();
		flags[3] = getProfessorRes.isBeforeFirst();
		getProfessorRes.close();

		this.getTA.setInt(1, id);
		final var getTARes = this.getTA.executeQuery();
		flags[4] = getTARes.isBeforeFirst();
		getTARes.close();

		this.getAdmin.setInt(1, id);
		final var getAdminRes = this.getAdmin.executeQuery();
		flags[5] = getAdminRes.isBeforeFirst();
		getAdminRes.close();

		return flags;
	}

	public void addProfessor(int id, String firstName, String lastName, String birthDate, String department) throws SQLException
	{
		this.connection.rollback();
		this.insertPerson.setInt(1, id);
		this.insertPerson.setString(2, firstName);
		this.insertPerson.setString(3, lastName);
		this.insertPerson.setString(4, birthDate);
		this.insertPerson.executeUpdate();

		this.insertEmployee.setInt(1, id);
		this.insertEmployee.setString(2, department);
		this.insertEmployee.executeUpdate();

		this.insertProfessor.setInt(1, id);
		this.insertProfessor.executeUpdate();

		this.connection.commit();
	}

	public void updateProfessor(int id, String firstName, String lastName, String birthDate, String department) throws SQLException
	{
		this.connection.rollback();
		this.updatePerson.setInt(1, id);
		this.updatePerson.setString(2, firstName);
		this.updatePerson.setString(3, lastName);
		this.updatePerson.setString(4, birthDate);
		this.updatePerson.executeUpdate();

		this.updateEmployee.setInt(1, id);
		this.updateEmployee.setString(2, department);
		this.updateEmployee.executeUpdate();

		this.connection.commit();
	}

	public Prof removeProfessor(int id) throws SQLException
	{
		this.connection.rollback();

		this.getProfessor.setInt(1, id);
		final var getProfRes = this.getProfessor.executeQuery();
		if(!getProfRes.isBeforeFirst())
		{
			// Professor does not exist
			throw new SQLException("Tried to remove professor, but professor does not exist");
		}
		getProfRes.next();
		final var old = new Prof(
			id,
			getProfRes.getString("department"),
			getProfRes.getString("first_name"),
			getProfRes.getString("last_name"),
			getProfRes.getString("birth_date"));
		getProfRes.close();

		this.deleteProfessor.setInt(1, id);
		this.deleteProfessor.executeUpdate();
		
		this.deleteEmployee.setInt(1, id);
		this.deleteEmployee.executeUpdate();
		
		this.deletePerson.setInt(1, id);
		this.deletePerson.executeUpdate();

		this.connection.commit();
		return old;
	}
}