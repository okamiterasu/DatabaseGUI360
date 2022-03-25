import java.awt.event.* ;
import java.sql.SQLException;
import javax.swing.* ;

//initial code from final project from another class
public class startup implements ActionListener{
	private static JLabel Userlabel;
	private static JLabel intro ; 
	private static JLabel message ; 
	public static JTextField userText;
	public static JButton LogInbutton;
	public static JButton Exit ; 
	private static JLabel LogTrue;
	static int LogSuccess;

	private static Database db;
	
	public static void main(String[] args) throws Exception{

		//this is a push to test github
		db = new Database("project.db");
		
		
		JFrame frame = new JFrame("startup") ; 
		JPanel panel = new JPanel();
    	frame.setSize(550, 200);
    	frame.setLocationRelativeTo(null);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.add(panel);
    	panel.setLayout(null);
    	
    	intro = new JLabel("THIS SOFTWARE IS NOT USED TO BE USED FOR UNIVERSITY MANAGEMENT PURPOSE") ; 
    	intro.setBounds(10,10,600,10) ; 
    	panel.add(intro) ;
    	
    	message = new JLabel("Please enter your ID") ; 
    	message.setBounds(100,70,600,10) ; 
    	panel.add(message) ;
    	
    	Userlabel = new JLabel("ID: ");
    	Userlabel.setBounds(70, 40, 80, 25); 
    	panel.add(Userlabel);
    	
    	
    	userText = new JTextField(20);
    	userText.setBounds(100, 40, 165, 25);
    	panel.add(userText) ; 
    	
        LogInbutton = new JButton("Log In");
    	LogInbutton.setBounds(300, 40, 100, 25);
    	LogInbutton.addActionListener(new startup());
    	panel.add(LogInbutton);
    	
    	Exit = new JButton("Exit") ; 
    	Exit.setBounds(300,70,100,25);
    	Exit.addActionListener(new exit());
    	panel.add(Exit) ; 
    	
    	LogTrue = new JLabel("");
    	LogTrue.setBounds(10,110,300,25);
    	panel.add(LogTrue);
    	frame.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String user = userText.getText() ; 
		
		try {
			final boolean[] flags = this.db.checkLogin(Integer.parseInt(user));
			final boolean accountExists = flags[0];
			final boolean isStudent = flags[1];
			final boolean isEmployee = flags[2];
			final boolean isTeacher = flags[3];
			final boolean isTA = flags[4];
			final boolean isAdmin = flags[5];

			if(isAdmin) {
				admin a = new admin() ;
				a.ad();
			} else if(!accountExists) {
				//will tell the user to try again
				LogTrue.setText("ID NOT Recognized, Please Try Again");
			}
		} catch(SQLException ex) {
			System.out.println(ex);
		}


		// if(user.equals("admin")) {
		// 	admin a = new admin() ;
		// 	a.ad() ; 
		// }
		
		// else {
		// 	//will tell the user to try again
		// 	LogTrue.setText("ID NOT Recognized, Please Try Again");
			
		// }
		
	}
	

}
