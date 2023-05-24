package RegionManagers.SocketManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FtpUtils {
    // FTP服务器采用匿名登录模式
    public String hostname = "192.168.43.125";
    public int port = 21;
    public String username = "anonymous";
    public String password = "";
    private static final int BUFFER_SIZE = 1024 * 1024 * 4;
    public FTPClient ftpClient = null;

	/**
	 * 测试用主函数
	 * 
	*/
	public static void main(String[] args) {
		FtpUtils ftpUtils = new FtpUtils();
		ftpUtils.hostname = "10.192.190.200";
		ftpUtils.username = "anonymous";
		ftpUtils.password = "";
		ftpUtils.port = 21;
		// ftpUtils.login();
		String localFilePath = "testhaha";
    	String remoteDirectory = "/tes";
   	 	boolean success = ftpUtils.uploadFile(localFilePath,remoteDirectory);
  		if (success) {
			System.out.println("文件上传成功！");
		} else {
			System.out.println("文件上传失败！");
		}
		ftpUtils.closeConnect();
	}

	public boolean login_test(){
		boolean flag = false;
		if(login())
			flag=true;
		else closeConnect();
		return flag;
	}

    private boolean login() {
		ftpClient = new FTPClient();
		boolean flag = false;
		ftpClient.setControlEncoding("utf-8");
		try {
			ftpClient.connect(hostname, port);
			flag = ftpClient.login(username, password);
			if (flag) {
				flag = ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				ftpClient.setBufferSize(BUFFER_SIZE);
				int reply = ftpClient.getReplyCode();
				if (!FTPReply.isPositiveCompletion(reply)) {
					closeConnect();
					System.out.println("FTP服务器连接失败");
					flag = false;
				}
			} else {
				System.out.println("FTP登录失败");
			}
		} catch (Exception e) {
			System.out.println("FTP登录失败" + e.getMessage());
			flag = false;
		}
		return flag;
	}

	private void closeConnect() {
		if (ftpClient != null) {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException e) {
				System.out.println("关闭FTP连接失败" + e.getMessage());
			}
		}
	}
	
	public Boolean downLoadFile(String ftpPath, String fileName, String savePath) {
		if (!login()) {
			return false;
		}
	
		if (ftpClient != null) {
			try {
				if (!ftpClient.changeWorkingDirectory(ftpPath)) {
					System.out.println("/" + ftpPath + "该目录不存在");
					return false;
				}
				ftpClient.enterLocalPassiveMode();
	
				FTPFile[] ftpFiles = ftpClient.listFiles();
	
				if (ftpFiles == null || ftpFiles.length == 0) {
					System.out.println("/" + ftpPath + "该目录下无文件");
					return false;
				}
				for (FTPFile file : ftpFiles) {
					if (fileName.equals("") || fileName.equalsIgnoreCase(file.getName())) {
						if (!file.isDirectory()) {
							File saveFile = new File(savePath + file.getName());
							try (OutputStream os = new FileOutputStream(saveFile)) {
								ftpClient.retrieveFile(file.getName(), os);
							}
						}
					}
				}
				return true;
			} catch (IOException e) {
				System.out.println("下载文件失败" + e.getMessage());
			} finally {
				closeConnect();
			}
		}
		return false;
	}
	
	public boolean additionalDownloadFile(String ftpPath, String fileName) {
		if (!login()) {
			return false;
		}
	
		if (ftpClient != null) {
			try {
				if (!ftpClient.changeWorkingDirectory(ftpPath)) {
					System.out.println("/" + ftpPath + "该目录不存在");
					return false;
				}
				ftpClient.enterLocalPassiveMode();
	
				FTPFile[] ftpFiles = ftpClient.listFiles();
	
				if (ftpFiles == null || ftpFiles.length == 0) {
					System.out.println("/" + ftpPath + "该目录下无文件");
					return false;
				}
				for (FTPFile file : ftpFiles) {
					if (fileName.equals("") || fileName.equalsIgnoreCase(file.getName())) {
						if (!file.isDirectory()) {
							File saveFile = new File(file.getName().split("#")[1]);
							try (OutputStream os = new FileOutputStream(saveFile, true)) {
								ftpClient.retrieveFile(file.getName(), os);
							}
						}
					}
				}
				return true;
			} catch (IOException e) {
				System.out.println("下载文件失败" + e.getMessage());
			} finally {
				closeConnect();
			}
		}
		return false;
	}
	
	public boolean uploadFile(String fileName, String savePath) {
		if (!login()) {
			return false;
		}
	
		boolean flag = false;
		if (ftpClient != null) {
			try (InputStream inputStream = new FileInputStream(new File(fileName))) {
				flag = ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				flag = ftpClient.makeDirectory(savePath);
				flag = ftpClient.changeWorkingDirectory(savePath);
				flag = ftpClient.storeFile(fileName, inputStream);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeConnect();
			}
		}
		return flag;
	}
	
	public boolean uploadFile(String fileName, String IP, String savePath) {
		if (!login()) {
			return false;
		}
	
		boolean flag = false;
		if (ftpClient != null) {
			try (InputStream inputStream = new FileInputStream(new File(fileName))) {
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				ftpClient.makeDirectory(savePath);
				ftpClient.changeWorkingDirectory(savePath);
				ftpClient.storeFile(fileName, inputStream);
				ftpClient.rename(fileName, "/catalog/" + IP + "#" + fileName);
				flag = true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeConnect();
			}
		}
		return flag;
	}
	
	public boolean deleteFile(String fileName, String filePath) {
		if (!login()) {
			return false;
		}
	
		boolean flag = false;
		if (ftpClient !=null) {
			try {
				ftpClient.changeWorkingDirectory(filePath);
				ftpClient.dele(fileName);
				flag = true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeConnect();
			}
		}
		return flag;
	}
}