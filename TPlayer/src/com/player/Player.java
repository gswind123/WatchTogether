/*
 * ����:lightp2p@gmail.com
 * ��վ:http://hi.baidu.com/mqlayer
 */

package com.player;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sun.swing.StringUIClientPropertyKey;
import net.miginfocom.swing.MigLayout;

import com.player.common.StringUtil;
import com.player.live.TLiveWindow;
import com.player.widget.PlayerProgressBar;
import com.player.widget.VideoPanel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Player implements WindowListener{
	static Config config;
	static JFrame mainFrame;

	VideoPanel videoPalel;
	JPanel controlBar;
	JButton button_Open;
	JButton button_Play;
	JButton button_Stop;
	PlayerProgressBar progressBar;
	JSlider volumeSlider;
	JTextArea playTimeView;

	String [] cmd ;
	Process proc ;
	Thread t1;
	Thread t2;

	String mplayerPath=".\\mplayer\\mplayer.exe";//mplayer����·��
	String mediaPath="";//�����ļ�·��
	double vedioLength=0;//��Ƶ����
	int videoWidth;//��Ƶ��
	int videoHeight;//��Ƶ��
	float rate=1.2f;//��߱�
	double playOffset=0;//��ǰ����ʱ��
	int volume=100;//����

	//������״̬
	boolean isPlay=false;
	boolean isPause=false;
	
	/** Gettters */
	public float getScreenRate(){
		return rate;
	}
	public double getVedioLength(){
		return vedioLength;
	}
	public double getCurrentOffset(){
		return playOffset;
	}

	public static void main(String[] args) {
		//���ô�����ʽ
		try {
			String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//ʵ����һ��������
		Player player=new Player();

		mainFrame=new JFrame("TPlayer");
		mainFrame.setBounds(config.playerBound);
		mainFrame.addWindowListener(player);
		Container cp = mainFrame.getContentPane();
		cp.setLayout(new MigLayout("insets 0 0 0 0"));

		//��Ӳ�������Ƶ���
		cp.add(player.getVideoPanel(),"width :2000:,height :2000:,wrap");
		//��Ӳ������������
		cp.add(player.getControlBar(),"");

		mainFrame.setVisible(true);
	}

	Player(){
		loadConfig();

		videoPalel=new VideoPanel(this);

		controlBar=new JPanel();
		controlBar.setLayout(new MigLayout());

		progressBar=new PlayerProgressBar(this);
		controlBar.add(progressBar,"width :2000:,span,wrap");

		JPanel p2=new JPanel();
		p2.setLayout(new MigLayout("insets 0 0 0 0,align center"));
		controlBar.add(p2,"width :2000:");

		button_Open=new JButton("Open...");
		p2.add(button_Open);
		button_Open.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fileChooser = new JFileChooser(config.lastOpenPath);
				fileChooser.setDialogTitle("���ļ�");
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setMultiSelectionEnabled(true);
				int status = fileChooser.showOpenDialog(button_Open);
				if (status == JFileChooser.APPROVE_OPTION) {
					final File[] selectedFile = fileChooser.getSelectedFiles();
					for(File f:selectedFile){
						config.lastOpenPath=f.getParent();
						initPlay(f.getAbsolutePath());
					}
				}
			}
		});

		button_Play=new JButton("Play");
		p2.add(button_Play);
		button_Play.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				if(isPlay){
					pause();
				}else if(mediaPath!=null){
					continuePlay();
				}
			}

		});

		button_Stop=new JButton("Stop");
		p2.add(button_Stop);
		button_Stop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
//				stop();
				TLiveWindow window = new TLiveWindow();
				window.showWindow();
			}
		});

		volumeSlider=new JSlider();
		p2.add(volumeSlider,"width ::70");
		volumeSlider.setValue(volume);
		volumeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if ((JSlider) e.getSource() == volumeSlider) {
					setVolume(volumeSlider.getValue());
					volume=volumeSlider.getValue();
				}

			}
		});
		
		//��ǰ����ʱ��
		playTimeView = new JTextArea();
		playTimeView.setBorder(null);
		playTimeView.setBackground(new Color(0xffffffff));
		playTimeView.setEditable(false);
		controlBar.add(playTimeView);
		refreshPlayTime();

		//ˢ�²��Ž�����ʾ
		Thread setProgressDelay = new Thread() {
			public void run() {
				while (true) {
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							if (progressBar != null) {
								if(mediaPath!=null){
									progressBar.setTime(playOffset, vedioLength);
									refreshPlayTime();
								}
							}
						}
					});
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		};
		setProgressDelay.start();
	}

	private void refreshPlayTime() {
		String playTimeStr = StringUtil.parseTime((int)(playOffset));
		String vedioTimeStr = StringUtil.parseTime((int)(vedioLength));
		playTimeView.setText(playTimeStr + "/" + vedioTimeStr);
	}
	
	/**
	 * ��һ���ļ�����������
	 */
	private void initPlay(String path){
		stop();
		mediaPath=path;
		isPlay=true;
		button_Play.setText("Pause");
		//����������,����ѡ����ο�mplayer�ĵ�
		cmd = new String[] {
				mplayerPath,//mplayer·��
				"-vo","directx",//��Ƶ����
				"-identify", //�������
				"-slave", //slaveģʽ����
				"-wid",String.valueOf(videoPalel.getWid()),//��Ƶ���ڵ�window handle
				"-colorkey", "0x030303",//��Ƶ���ڵı���ɫ
				path //�����ļ�·��
		};
		try {
			proc = Runtime.getRuntime().exec(cmd);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

		//��ȡ������mplayer�������Ϣ
		final InputStream is1 = proc.getErrorStream();
		final InputStream is2 = proc.getInputStream();
		final Runnable errorReader = new Runnable() {
			public void run() {
				try {
					final BufferedReader lReader = new BufferedReader(new InputStreamReader(is1));
					for (String l = lReader.readLine(); l != null ; l = lReader.readLine()) {
						// System.out.println("ERROR "+l);
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		};


		final Runnable standReader = new Runnable() {
			public void run() {
				try {
					final BufferedReader lReader = new BufferedReader(new InputStreamReader(is2));
					String l="";
					while ((l=lReader.readLine())!=null) {
						if (l.length() >= 10) {
							String s2 = l.substring(0, 9);
							if (s2.equals("ID_LENGTH")) {
								int index = l.indexOf(".");
								String s1 = l.substring(10,index);
								vedioLength = Integer.valueOf(s1);
							}
						}
						//��������ʱ��
						if(l.length()>70&&l.startsWith("A:")){
							Pattern floatPattern = Pattern.compile("[0-9]+\\.[0-9]+");
							Matcher timeMatcher = floatPattern.matcher(l);
							if(timeMatcher.find()) {
								playOffset = Double.parseDouble(timeMatcher.group(0));
							}
						}
						if (l.length() >= 15) {
							String s4 = l.substring(0, 15);
							//������Ƶ���
							if (s4.equals("ID_VIDEO_HEIGHT")) {
								String s1 = l.substring(16);
								videoHeight = Integer.valueOf(s1);
								rate = (float) videoWidth/ (float) videoHeight;
								videoPalel.doLayout();
							}
						}
						if (l.length() >= 14) {
							String s3 = l.substring(0, 14);
							//������Ƶ�߶�
							if (s3.equals("ID_VIDEO_WIDTH")) {
								String s1 = l.substring(15);
								videoWidth = Integer.valueOf(s1);
							}
						}

						if (l.startsWith("ID_LENGTH")) {
							int index = l.indexOf("=");
							//������Ƶ����
							if (index > 0) {
								String value = l.substring(index + 1);
								float intvalue=Float.valueOf(value);
								vedioLength=(int)intvalue;
							}
						}
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		};

		t1 = new Thread(errorReader);
		t2 = new Thread(standReader);
		t1.start();
		t2.start();

		//����mplayer�˳�
		Thread waitThread=new Thread (){
			public void run(){
				try {
					proc.waitFor();
					t1.interrupt();
					t2.interrupt();
					playComplete();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		};
		waitThread.start();
	}

	//����ʱ��
	public void seekto(int value) {
		if(isPlay){
			isPause=false;
			button_Play.setText("Pause");
			if (proc != null&isPlay) {
				PrintStream s = new PrintStream(proc.getOutputStream());
				String command = "seek " + value + " 2\n";
				s.print(command);
				s.flush();
			}
		}
	}

	//��������
	public void setVolume(int vol) {
		if (proc != null) {
			if (!isPause) {
				PrintStream s = new PrintStream(proc.getOutputStream());
				s.print("volume " + vol + " 1\n");
				s.flush();
			}
		}
	}

	//ֹͣ
	public void stop(){
		if(proc!=null){
			proc.destroy();
		}
		playOffset=0;
		isPause=false;
		button_Play.setText("Play");
		progressBar.setValue(0);
	}

	//��ͣ
	public void pause(){
		if (proc != null) {
			PrintStream s = new PrintStream(proc.getOutputStream());
			s.print("pause\n");
			s.flush();
			isPause=!isPause;
		}
		if(isPause){
			button_Play.setText("Play");
		}else{
			button_Play.setText("Pause");
		}

	}
	
	//����
	public void continuePlay() {
		if(proc != null) {
			PrintStream s = new PrintStream(proc.getOutputStream());
			s.print("continue\n");
			s.flush();
			isPause = false;
		}
		button_Play.setText("Pause");
	}

	//���Ž���
	void playComplete(){
		isPlay=false;
		isPause=false;
		button_Play.setText("Play");
		playOffset=0;
	}

	Container getVideoPanel(){
		return videoPalel;
	}

	Container getControlBar(){
		return controlBar;
	}

	//�˳�����
	void exit(){
		if(proc!=null){
			proc.destroy();
		}
		saveConfig();
		System.exit(0);
	}

	//��������
	void loadConfig(){
		FileInputStream bytetIn;
		try {
			bytetIn = new FileInputStream(new File("config3.ob"));
			ObjectInputStream Iner = new ObjectInputStream(bytetIn);
			Object ob=Iner.readObject();
			Iner.close();
			config=(Config)ob;
		} catch (Exception e) {
			//e.printStackTrace();
			config=new Config();
		}
		mediaPath=config.lastPlayPath;
	}

	//��������
	void saveConfig(){
		config.playerBound=mainFrame.getBounds();
		config.lastPlayPath=mediaPath;
		File file = new File("config3.ob");
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		try {
			FileOutputStream bytetOut;
			bytetOut = new FileOutputStream(file);
			ObjectOutputStream outer = new ObjectOutputStream(bytetOut);
			outer.writeObject(config);
			outer.flush();
			outer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void windowActivated(WindowEvent e) {

	}

	public void windowClosed(WindowEvent e) {

	}

	public void windowClosing(WindowEvent e) {
		exit();
	}

	public void windowDeactivated(WindowEvent e) {

	}

	public void windowDeiconified(WindowEvent e) {

	}

	public void windowIconified(WindowEvent e) {

	}

	public void windowOpened(WindowEvent e) {

	}

}
