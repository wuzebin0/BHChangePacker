package cn.bh.jc.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.bh.jc.common.PathUtil;
import cn.bh.jc.handle.SVNWorker;

/**
 * SVN变化版本
 * 
 * @author liubq
 * @since 2018年1月16日
 */
public class SVNVersion extends StoreVersion {
	// svn地址
	private final String svnUrl;
	// 用户名称
	private final String username;
	// 用户密码
	private final String password;
	// 开始版本
	private final Long startVersion;
	// 结束版本
	private final Long endVersion;
	// 项目名称
	private final String projectName;
	// 工作者
	private final SVNWorker worker;

	/**
	 * SVN变化版本
	 * 
	 * @param target 可运行程序（编译后程序）保存地址
	 * @param inSvnUrl svn 地址
	 * @param name 用户名称
	 * @param pwd 用户密码
	 * @param startVersion 开始版本号
	 * @throws Exception
	 */
	public SVNVersion(String target, String inSvnUrl, String name, String pwd, Long startVersion) throws Exception {
		super();
		this.setTargetPath(target);
		this.svnUrl = inSvnUrl;
		String tempSvnUrl = PathUtil.replace(svnUrl);
		this.projectName = tempSvnUrl.substring(tempSvnUrl.lastIndexOf("/") + 1);
		// 检查
		if (!this.getTargetPath().endsWith(projectName)) {
			throw new Exception("可执行工程(" + target + ") 和目标SVN工程(" + inSvnUrl + ")的工程名称不统一，目前该系统处理不了 ");
		}
		this.username = name;
		this.password = pwd;
		this.startVersion = startVersion;
		this.endVersion = -1L;
		worker = new SVNWorker(this);
	}

	/**
	 * 列出所有变化文件
	 * 
	 * @return
	 * @throws Exception
	 */
	public ChangeVO get() throws Exception {
		// 变化的文件列表
		ChangeInfo svnInfo = worker.listAllChange();
		if (svnInfo == null) {
			return null;
		}
		ChangeVO resVO = new ChangeVO();
		resVO.setVersion(this);
		resVO.setInfo(new ChangeInfo());
		// SVN，地址转换为标准地址
		// 变化文件转换
		List<String> changeFiles = new ArrayList<String>();
		for (String svnFileName : svnInfo.getChangeFiles()) {
			changeFiles.add(PathUtil.trimName(svnFileName, this.projectName));
		}
		resVO.getInfo().setChangeFiles(changeFiles);
		// 删除文件转换
		Set<String> delSet = new HashSet<String>();
		for (String svnFileName : svnInfo.getDelSet()) {
			delSet.add(PathUtil.trimName(svnFileName, this.projectName));
		}
		resVO.getInfo().setDelSet(delSet);
		return resVO;
	}

	public String getSvnUrl() {
		return svnUrl;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public Long getStartVersion() {
		return startVersion;
	}

	public Long getEndVersion() {
		return endVersion;
	}

	public String getProjectName() {
		return projectName;
	}

	public SVNWorker getWorker() {
		return worker;
	}

}
