/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2012, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.connections.install;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.cic.agent.core.api.IProfile;

public class ConsoleNotificationPanel extends BaseConfigConsolePanel {
	String className = ConsoleNotificationPanel.class.getName();
	private static final String nonSSLPort = "25";
	private static final String SSLPort = "465";

	private final ILogger log = IMLogger.getLogger(this.getClass().getName());

	private IProfile profile = null; // profile to save data in
	/** 1 for Notification only,2 for Notification and ReplyTo, 3 for none */
	private int option = 1;
	InstallValidator installValidator = new InstallValidator();

	/**
	 * notification configuration for 'Enable Notification Only'. 1 for WAS Java
	 * Mail, 2 for DNS
	 */
	private int notifConfigType1 = 1;
	/**
	 * notification configuration for 'Enable Notification and ReplyTo'. 1 for
	 * WAS Java Mail, 2 for DNS
	 */
	private int notifConfigType2 = 1;
	// for notification only
	private String smtpHostNameJavaMail1 = "example.com";
	private String smtpUserIdJavaMail1 = "";
	private String smtpUserPwJavaMail1 = "";
	private boolean enableSSLJavaMail1 = false;
	private String portJavaMail1 = "";
	private boolean enableAuthJavaMail1 = false;

	private String smtpHostNameJavaMail2 = "example.com";
	private String smtpUserIdJavaMail2 = "";
	private String smtpUserPwJavaMail2 = "";
	private boolean enableSSLJavaMail2 = false;
	private String portJavaMail2 = "";
	private boolean enableAuthJavaMail2 = false;

	private String smtpDomaiNameDNS1 = "";
	private String smtpUserIdDNS1 = "";
	private String smtpUserPwDNS1 = "";
	private boolean enableSSLDNS1 = false;
	private String portDNS1 = "";
	private boolean enableAuthDNS1 = false;
	private boolean enableServerInfoDNS1 = false;

	private String smtpDomaiNameDNS2 = "";
	private String smtpUserIdDNS2 = "";
	private String smtpUserPwDNS2 = "";
	private boolean enableSSLDNS2 = false;
	private String portDNS2 = "";
	private boolean enableAuthDNS2 = false;
	private boolean enableServerInfoDNS2 = false;

	private String dnsServerHost1 = "";
	private String dnsServerPort1 = "53";
	private String dnsServerHost2 = "";
	private String dnsServerPort2 = "53";

	private String domainName1 = "example.com";
	private String localPart1 = "";
	private String prefix1 = "prefix_";
	private String suffix1 = "_suffix";
	private String mailFileServer1 = "example.com";
	private String mailFileUserID1 = "";
	private String mailFilePW1 = "";
	/** 1 for None, 2 for Prefix, 3 for suffix */
	private int emailAddressOptJavaMail = 1;

	private String domainName2 = "example.com";
	private String localPart2 = "";
	private String prefix2 = "prefix_";
	private String suffix2 = "_suffix";
	private String mailFileServer2 = "";
	private String mailFileUserID2 = "";
	private String mailFilePW2 = "";
	/** 1 for None, 2 for Prefix, 3 for suffix */
	private int emailAddressOptDNS = 1;

	public ConsoleNotificationPanel() {
		super(Messages.Notification_TITLE);
	}

	@Override
	public void perform() {
		if (shouldSkip())
			return;
		log.info("Notification Panel :: Entered");
		TextCustomPanelUtils.setLogPanel(log, "Notification panel");
		TextCustomPanelUtils.showNotice(Messages.NOTICE_PREVIOUS);
		TextCustomPanelUtils.showTitle(Messages.COMMON_CONFIG,
				Messages.Notification_TITLE);
		option = TextCustomPanelUtils
				.singleSelect(Messages.Notification_DES, new String[] {
						Messages.Notification_ENABLE_NOTIFICATION_ONLY,
						Messages.Notification_ENABLE_NOTIFICATION_AND_REPLYTO,
						Messages.Notification_NOT_ENABLED }, option, null, null);
		TextCustomPanelUtils
				.logInput(
						"notification mode",
						option == 1 ? Messages.Notification_ENABLE_NOTIFICATION_ONLY
								: option == 2 ? Messages.Notification_ENABLE_NOTIFICATION_AND_REPLYTO
										: Messages.Notification_NOT_ENABLED);
		// configure notification
		if (option != 3)
			startConfiguration();
		else {
			setUserDataNone();
			String input = TextCustomPanelUtils.getInput(Messages.GOTO_NEXT,
					Messages.NEXT_INDEX, new String[] {
							Messages.PREVIOUS_INPUT_INDEX,
							Messages.BACK_TO_TOP_INDEX, Messages.NEXT_INDEX });
			if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)
					|| input.trim()
							.equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX)) {
				perform();
			}
		}
	}

	/** configure notification */
	private void startConfiguration() {
		TextCustomPanelUtils.showSubTitle1(Messages.Notification_TITLE);
		int input = TextCustomPanelUtils.singleSelect(
				Messages.Notification_CHOOSE_INFO, new String[] {
						Messages.Notification_WAS_JAVA_MAIL,
						Messages.Notification_DNS_INFO },
				option == 1 ? notifConfigType1 : notifConfigType2,
				new String[] { Messages.PREVIOUS_INPUT_INDEX }, null);
		if (input < 0) {
			perform();
			return;
		}
		if (option == 1) {
			notifConfigType1 = input;
			if (notifConfigType1 == 1) {
				configSmtpJavaMail1();
			} else
				configDNSInfo1();
		} else if (option == 2) {
			notifConfigType2 = input;
			if (notifConfigType2 == 1)
				configSmtpJavaMail2();
			else
				configDNSInfo2();
		}

	}

	private void configSmtpJavaMail1() {
		TextCustomPanelUtils
				.showSubTitle2(Messages.Notification_JAVA_MAIL_TITLE);
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_SMTP_HOST_NAME, smtpHostNameJavaMail1);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			startConfiguration();
			return;
		}
		TextCustomPanelUtils.logInput("smtp host name java mail", input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_JAVAEMAIL_INVALID_SMTP_HOSTNAME_INPUT_WARNING);
			log.error("CLFRP0059E: Invalid host name of SMTP messaging server.");
			configSmtpJavaMail1();
			return;
		}
		smtpHostNameJavaMail1 = input.trim();
		decideToAuthSmtpJavaMail1();
	}

	private void decideToAuthSmtpJavaMail1() {
		String javaMailAuth = TextCustomPanelUtils.showYorN(
				Messages.Notification_SMTP_AUTH_INFO, enableAuthJavaMail1 ? 0
						: 1, new String[] { Messages.PREVIOUS_INPUT_INDEX });
		if (javaMailAuth.trim().toUpperCase()
				.equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configSmtpJavaMail1();
			return;
		} else
			enableAuthJavaMail1 = javaMailAuth.equals("Y") ? true : false;
		TextCustomPanelUtils.logInput("enable authentication java mail",
				javaMailAuth);
		if (enableAuthJavaMail1) {
			configSmtpUserIdJavaMail1();
		} else
			decideToEncryptSSLJavaMail1();
	}

	private void configSmtpUserIdJavaMail1() {
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_SMTP_USER_ID, smtpUserIdJavaMail1);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			decideToAuthSmtpJavaMail1();
			return;
		}
		TextCustomPanelUtils.logInput("smtp user id java mail", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_JAVAEMAIL_INVALID_SMTP_USERID_INPUT_WARNING);
			log.error("CLFRP0060E: Invalid user ID of SMTP messaging server.");
			configSmtpUserIdJavaMail1();
			return;
		}
		smtpUserIdJavaMail1 = input.trim();
		configSmtpUserPwJavaMail1();
	}

	private void configSmtpUserPwJavaMail1() {
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_SMTP_PASSWORD, null);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			configSmtpUserIdJavaMail1();
			return;
		}
		if (!verifyPasswordComplete(input.trim())) {
			configSmtpUserPwJavaMail1();
			return;
		}
		smtpUserPwJavaMail1 = input.trim();
		decideToEncryptSSLJavaMail1();
	}

	private void decideToEncryptSSLJavaMail1() {
		String javaMailEncrypt = TextCustomPanelUtils.showYorN(
				Messages.Notification_SMTP_ENCRYPT, enableSSLJavaMail1 ? 0 : 1,
				new String[] { Messages.PREVIOUS_INPUT_INDEX });
		if (javaMailEncrypt.trim().toUpperCase()
				.equals(Messages.PREVIOUS_INPUT_INDEX)) {
			if (enableAuthJavaMail1)
				configSmtpUserPwJavaMail1();
			else
				decideToAuthSmtpJavaMail1();
			return;
		} else
			enableSSLJavaMail1 = javaMailEncrypt.equals("Y") ? true : false;
		TextCustomPanelUtils.logInput("encrypt SSL java mail", javaMailEncrypt);
		configSMTPPort1(0);
	}

	/** 0 for Java Mail, 1 for DNS . End of 'Enable Notification Only' */
	private void configSMTPPort1(int option) {
		String input = null;
		if (option == 0) {
			input = TextCustomPanelUtils.getInput(
					Messages.Notification_SMTP_PORT_INFO,
					enableSSLJavaMail1 ? SSLPort : nonSSLPort);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				decideToEncryptSSLJavaMail1();
				return;
			}
			TextCustomPanelUtils.logInput("smtp port java mail", input);
			if (!verifyPortComplete(input.trim())) {
				TextCustomPanelUtils
						.showError(Messages.Notificaton_JAVAEMAIL_INVALID_SMTP_PORT_INPUT_WARNING);
				log.error("CLFRP0062E: Invalid port number of SMTP messaging server.");
				configSMTPPort1(0);
				return;
			}
			portJavaMail1 = input.trim();
			setUserDataJavaMail(smtpHostNameJavaMail1, portJavaMail1,
					smtpUserIdJavaMail1, smtpUserPwJavaMail1,
					enableSSLJavaMail1);
		} else if (option == 1) {
			input = TextCustomPanelUtils.getInput(
					Messages.Notification_SMTP_PORT_INFO,
					enableSSLDNS1 ? SSLPort : nonSSLPort);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				decideToEncryptSSLDNS1();
				return;
			}
			TextCustomPanelUtils.logInput("smtp port DNS", input);
			if (!verifyPortComplete(input.trim())) {
				TextCustomPanelUtils
						.showError(Messages.Notificaton_DNS_INVALID_SMTP_PORT_INPUT_WARNING);
				log.error("CLFRP0062E: Invalid port number of SMTP messaging server.");
				configSMTPPort1(1);
				return;
			}
			portDNS1 = input.trim();
			setUserDataDNS(dnsServerHost1, dnsServerPort1, smtpDomaiNameDNS1,
					portDNS1, smtpUserIdDNS1, smtpUserPwDNS1, enableSSLDNS1);

		}
		goToNext1(option);
	}

	/** Go to next for 'Enable Notification Only' */
	private void goToNext1(int opt) {
		String input = TextCustomPanelUtils.getInput(Messages.GOTO_NEXT,
				Messages.NEXT_INDEX, new String[] {
						Messages.PREVIOUS_INPUT_INDEX,
						Messages.BACK_TO_TOP_INDEX, Messages.NEXT_INDEX });
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			configSMTPPort1(opt);
			return;
		}
		if (input.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX)) {
			perform();
		}
	}

	/** 0 for Java Mail, 1 for DNS */
	private void configSMTPPort2(int option) {
		String input = null;
		if (option == 0) {
			input = TextCustomPanelUtils.getInput(
					Messages.Notification_SMTP_PORT_INFO,
					enableSSLJavaMail2 ? SSLPort : nonSSLPort);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				decideToEncryptSSLJavaMail2();
				return;
			}
			TextCustomPanelUtils.logInput("smtp port java mail", input);
			if (!verifyPortComplete(input.trim())) {
				TextCustomPanelUtils
						.showError(Messages.Notificaton_JAVAEMAIL_INVALID_SMTP_PORT_INPUT_WARNING);
				log.error("CLFRP0062E: Invalid port number of SMTP messaging server.");
				configSMTPPort2(0);
				return;
			}
			portJavaMail2 = input.trim();
			setUserDataJavaMail(smtpHostNameJavaMail2, portJavaMail2,
					smtpUserIdJavaMail2, smtpUserPwJavaMail2,
					enableSSLJavaMail2);
			configReplyToJavaMail();
		} else if (option == 1) {
			input = TextCustomPanelUtils.getInput(
					Messages.Notification_SMTP_PORT_INFO,
					enableSSLDNS2 ? SSLPort : nonSSLPort);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				decideToEncryptSSLDNS2();
				return;
			}
			TextCustomPanelUtils.logInput("smtp port DNS", input);
			if (!verifyPortComplete(input.trim())) {
				TextCustomPanelUtils
						.showError(Messages.Notificaton_JAVAEMAIL_INVALID_SMTP_PORT_INPUT_WARNING);
				log.error("CLFRP0062E: Invalid port number of SMTP messaging server.");
				configSMTPPort2(1);
				return;
			}
			portDNS2 = input.trim();
			setUserDataDNS(dnsServerHost2, portDNS2, smtpDomaiNameDNS2,
					portDNS2, smtpUserIdDNS2, smtpUserPwDNS2, enableSSLDNS2);
			configReplyToDNS();
		}
	}

	private void configReplyToJavaMail() {
		TextCustomPanelUtils.showSubTitle2(Messages.Notification_REPLYTO_TITLE);
		TextCustomPanelUtils
				.showSubTitle3(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_TITLE);
		TextCustomPanelUtils
				.showText(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_DES);
		String input = TextCustomPanelUtils.getInput(
				Messages.Notificaton_REPLYTO_DOMAIN_NAME, domainName1);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			configSMTPPort2(0);
			return;
		}
		TextCustomPanelUtils.logInput("reply to domain name java mail", input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_DOMAIN_NAME_WARNING);
			log.error("CLFRP0053E: Invalid domain name.");
			configReplyToJavaMail();
			return;
		}
		domainName1 = input.trim();
		chooseEmailAddressTypeJavaMail();
	}

	private void chooseEmailAddressTypeJavaMail() {
		int input = TextCustomPanelUtils.singleSelect(
				Messages.Notificaton_REPLYTO_DOMAIN_NAME, new String[] {
						Messages.Notification_REPLYTO_NONE,
						Messages.Notification_REPLYTO_Prefix,
						Messages.Notification_REPLYTO_Suffix },
				emailAddressOptJavaMail,
				new String[] { Messages.PREVIOUS_INPUT_INDEX }, null);
		if (input < 0) {
			configReplyToJavaMail();
			return;
		}
		emailAddressOptJavaMail = input;
		configLocalPart1();
	}

	private void configLocalPart1() {
		String input = null;
		if (emailAddressOptJavaMail == 1) {
			TextCustomPanelUtils.showText(Messages.bind(
					Messages.Notification_REPLYTO_NONE_LABEL, domainName1));
			localPart1 = "";
		} else if (emailAddressOptJavaMail == 2) {
			input = TextCustomPanelUtils.getInput(Messages.bind(
					Messages.Notification_REPLYTO_Prefix_LABEL, prefix1,
					domainName1), prefix1);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				chooseEmailAddressTypeJavaMail();
				return;
			}
			TextCustomPanelUtils.logInput("java mail prefix", input);
			if (!verifyPrefixComplete(input.trim())) {
				TextCustomPanelUtils
						.showError(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_PREFIX_INPUT_WARNING);
				log.error("CLFRP0057E: Prefix input is incorrect.");
				configLocalPart1();
				return;
			}
			prefix1 = input.trim();
			localPart1 = prefix1;
		} else if (emailAddressOptJavaMail == 3) {
			input = TextCustomPanelUtils.getInput(Messages.bind(
					Messages.Notification_REPLYTO_Suffix_LABEL, suffix1,
					domainName1), suffix1);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				chooseEmailAddressTypeJavaMail();
				return;
			}
			TextCustomPanelUtils.logInput("java mail suffix", input);
			if (!verifySuffixComplete(input.trim())) {
				TextCustomPanelUtils
						.showError(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_SUFFIX_INPUT_WARNING);
				log.error("CLFRP0058E: Suffix input is incorrect.");
				configLocalPart1();
				return;
			}
			suffix1 = input.trim();
			localPart1 = suffix1;
		}
		if (input != null && input.trim().length() > 27) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_TOO_LONG_WARNING);
			log.error("Email address is too long.");
			configLocalPart1();
			return;
		}
		configMailFileInfoJavaMail();
	}

	private void configMailFileInfoJavaMail() {
		TextCustomPanelUtils
				.showSubTitle3(Messages.Notification_REPLYTO_MAIL_FILE_TITLE);
		TextCustomPanelUtils
				.showText(Messages.Notification_REPLYTO_MAIL_FILE_DES);
		String input = TextCustomPanelUtils
				.getInput(Messages.Notification_REPLYTO_MAIL_FILE_SERVER,
						mailFileServer1);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			if (emailAddressOptJavaMail != 1)
				configLocalPart1();
			else
				chooseEmailAddressTypeJavaMail();
			return;
		}
		TextCustomPanelUtils.logInput("replyto mail file server java mail",
				input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_MAILFILE_SERVER_WARNING);
			log.error("CLFRP0054E: Invalid Mail file server name.");
			configMailFileInfoJavaMail();
			return;
		}
		mailFileServer1 = input.trim();
		configMailFileUserIdJavaMail();
	}

	private void configMailFileUserIdJavaMail() {
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_REPLYTO_MAIL_FILE_USER_ID,
				mailFileUserID1);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			configMailFileInfoJavaMail();
			return;
		}
		TextCustomPanelUtils.logInput("replyto mail file user id java mail",
				input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_MAILFILE_USER_ID_WARNING);
			log.error("CLFRP0055E: Invalid Mail file user ID.");
			configMailFileUserIdJavaMail();
			return;
		}
		mailFileUserID1 = input.trim();
		configMailFileUserPwJavaMail();
	}

	/** end of 2.1 */
	private void configMailFileUserPwJavaMail() {
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_REPLYTO_MAIL_FILE_PW, null);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			configMailFileUserIdJavaMail();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_MAILFILE_PW_WARNING);
			log.error("CLFRP0056E: Invalid Mail file password.");
			configMailFileUserPwJavaMail();
			return;
		}
		mailFilePW1 = input.trim();
		String type = emailAddressOptJavaMail == 1 ? "none"
				: emailAddressOptJavaMail == 2 ? "prefix" : "suffix";
		String localPart = "";
		if (emailAddressOptJavaMail > 1)
			localPart = localPart1;
		setUserDataReplyTo(domainName1, type, localPart, mailFileServer1,
				mailFileUserID1, mailFilePW1);
		goToNext2();
	}

	/** End of 'Enable Notification and ReplyTo' Java Mail */
	private void goToNext2() {
		String input = TextCustomPanelUtils.getInput(Messages.GOTO_NEXT,
				Messages.NEXT_INDEX, new String[] {
						Messages.PREVIOUS_INPUT_INDEX,
						Messages.BACK_TO_TOP_INDEX, Messages.NEXT_INDEX });
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			configMailFileUserPwJavaMail();
			return;
		}
		if (input.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX)) {
			perform();
		}
	}

	private void configReplyToDNS() {
		TextCustomPanelUtils.showSubTitle2(Messages.Notification_REPLYTO_TITLE);
		TextCustomPanelUtils
				.showSubTitle3(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_TITLE);
		TextCustomPanelUtils
				.showText(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_DES);
		String input = TextCustomPanelUtils.getInput(
				Messages.Notificaton_REPLYTO_DOMAIN_NAME, domainName2);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			configSMTPPort2(1);
			return;
		}
		TextCustomPanelUtils.logInput("replyto domain name DNS", input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_DOMAIN_NAME_WARNING);
			log.error("CLFRP0053E: Invalid domain name.");
			configReplyToDNS();
			return;
		}
		domainName2 = input.trim();
		chooseEmailAddressTypeDNS();
	}

	private void chooseEmailAddressTypeDNS() {
		int input = TextCustomPanelUtils.singleSelect(
				Messages.Notificaton_REPLYTO_DOMAIN_NAME, new String[] {
						Messages.Notification_REPLYTO_NONE,
						Messages.Notification_REPLYTO_Prefix,
						Messages.Notification_REPLYTO_Suffix },
				emailAddressOptDNS,
				new String[] { Messages.PREVIOUS_INPUT_INDEX }, null);
		if (input < 0) {
			configReplyToDNS();
			return;
		}
		emailAddressOptDNS = input;
		configLocalPart2();
	}

	private void configLocalPart2() {
		String input = null;
		if (emailAddressOptDNS == 1) {
			TextCustomPanelUtils.showText(Messages.bind(
					Messages.Notification_REPLYTO_NONE_LABEL, domainName2));
			localPart2 = "";
		} else if (emailAddressOptDNS == 2) {
			input = TextCustomPanelUtils.getInput(Messages.bind(
					Messages.Notification_REPLYTO_Prefix_LABEL, prefix2,
					domainName2), prefix2);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				chooseEmailAddressTypeDNS();
				return;
			}
			TextCustomPanelUtils.logInput("mail prefix DNS", input);
			if (!verifyPrefixComplete(input.trim())) {
				TextCustomPanelUtils
						.showError(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_PREFIX_INPUT_WARNING);
				log.error("CLFRP0057E: Prefix input is incorrect.");
				configLocalPart2();
				return;
			}
			prefix2 = input.trim();
			localPart2 = prefix2;
		} else if (emailAddressOptDNS == 3) {
			input = TextCustomPanelUtils.getInput(Messages.bind(
					Messages.Notification_REPLYTO_Suffix_LABEL, suffix2,
					domainName2), suffix2);
			if (input.trim().toUpperCase()
					.equals(Messages.PREVIOUS_INPUT_INDEX)) {
				chooseEmailAddressTypeDNS();
				return;
			}
			TextCustomPanelUtils.logInput("mail suffix DNS", input);
			if (!verifySuffixComplete(input.trim())) {
				TextCustomPanelUtils
						.showError(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_SUFFIX_INPUT_WARNING);
				log.error("CLFRP0058E: Suffix input is incorrect.");
				configLocalPart2();
				return;
			}
			suffix2 = input.trim();
			localPart2 = suffix2;
		}
		if (input != null)
			if (input.trim().length() > 27) {
				TextCustomPanelUtils
						.showError(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_TOO_LONG_WARNING);
				log.error("Email address is too long.");
				configLocalPart2();
				return;
			}
		configMailFileInfoDNS();
	}

	private void configMailFileInfoDNS() {
		TextCustomPanelUtils
				.showSubTitle3(Messages.Notification_REPLYTO_MAIL_FILE_TITLE);
		TextCustomPanelUtils
				.showText(Messages.Notification_REPLYTO_MAIL_FILE_DES);
		String input = TextCustomPanelUtils
				.getInput(Messages.Notification_REPLYTO_MAIL_FILE_SERVER,
						mailFileServer2);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			if (emailAddressOptDNS != 1)
				configLocalPart2();
			else
				chooseEmailAddressTypeDNS();
			return;
		}
		TextCustomPanelUtils.logInput("replyto mail file server DNS", input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_MAILFILE_SERVER_WARNING);
			log.error("CLFRP0054E: Invalid Mail file server name.");
			configMailFileInfoDNS();
			return;
		}
		mailFileServer2 = input.trim();
		configMailFileUserIdDNS();
	}

	private void configMailFileUserIdDNS() {
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_REPLYTO_MAIL_FILE_USER_ID,
				mailFileUserID2);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			configMailFileInfoDNS();
			return;
		}
		TextCustomPanelUtils.logInput("replyto mail file user id DNS", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_MAILFILE_USER_ID_WARNING);
			log.error("CLFRP0055E: Invalid Mail file user ID.");
			configMailFileUserIdDNS();
			return;
		}
		mailFileUserID2 = input.trim();
		configMailFileUserPwDNS();
	}

	/** End of 'Enable Notification and ReplyTo' DNS */
	private void configMailFileUserPwDNS() {
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_REPLYTO_MAIL_FILE_PW, null);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			configMailFileUserIdDNS();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_REPLYTO_EMAIL_ADDRESS_INVALID_MAILFILE_PW_WARNING);
			log.error("CLFRP0056E: Invalid Mail file password.");
			configMailFileUserPwDNS();
			return;
		}
		mailFilePW2 = input.trim();
		String type = emailAddressOptDNS == 1 ? "none"
				: emailAddressOptDNS == 2 ? "prefix" : "suffix";
		String localPart = "";
		if (emailAddressOptDNS > 1)
			localPart = localPart2;
		setUserDataReplyTo(domainName2, type, localPart, mailFileServer2,
				mailFileUserID2, mailFilePW2);
		goToNext3();
	}

	/** Go to next of 'Enable Notification and ReplyTo' DNS */
	private void goToNext3() {
		String input = TextCustomPanelUtils.getInput(Messages.GOTO_NEXT,
				Messages.NEXT_INDEX, new String[] {
						Messages.PREVIOUS_INPUT_INDEX,
						Messages.BACK_TO_TOP_INDEX, Messages.NEXT_INDEX });
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			configMailFileUserPwDNS();
			return;
		}
		if (input.trim().equalsIgnoreCase(Messages.BACK_TO_TOP_INDEX)) {
			perform();
		}
	}

	private void configSmtpJavaMail2() {
		TextCustomPanelUtils
				.showSubTitle2(Messages.Notification_JAVA_MAIL_TITLE);
		TextCustomPanelUtils.showText(Messages.Notification_JAVA_MAIL_INFO);
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_SMTP_HOST_NAME, smtpHostNameJavaMail2);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			startConfiguration();
			return;
		}
		TextCustomPanelUtils.logInput("smtp host name java mail", input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_JAVAEMAIL_INVALID_SMTP_HOSTNAME_INPUT_WARNING);
			log.error("CLFRP0059E: Invalid host name of SMTP messaging server.");
			configSmtpJavaMail2();
			return;
		}
		smtpHostNameJavaMail2 = input.trim();
		decideToAuthSmtpJavaMail2();
	}

	private void decideToAuthSmtpJavaMail2() {
		String javaMailAuth = TextCustomPanelUtils.showYorN(
				Messages.Notification_SMTP_AUTH_INFO, enableAuthJavaMail2 ? 0
						: 1, new String[] { Messages.PREVIOUS_INPUT_INDEX });
		if (javaMailAuth.trim().toUpperCase()
				.equals(Messages.PREVIOUS_INPUT_INDEX)) {
			configSmtpJavaMail2();
			return;
		} else
			enableAuthJavaMail2 = javaMailAuth.equals("Y") ? true : false;
		TextCustomPanelUtils.logInput("enable smtp authentication java mail",
				javaMailAuth);
		if (enableAuthJavaMail2) {
			configSmtpUserIdJavaMail2();
		} else
			decideToEncryptSSLJavaMail2();
	}

	private void configSmtpUserIdJavaMail2() {
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_SMTP_USER_ID, smtpUserIdJavaMail2);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			decideToAuthSmtpJavaMail2();
			return;
		}
		TextCustomPanelUtils.logInput("smtp user id java mail", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_JAVAEMAIL_INVALID_SMTP_USERID_INPUT_WARNING);
			log.error("CLFRP0060E: Invalid user ID of SMTP messaging server.");
			configSmtpUserIdJavaMail2();
			return;
		}
		smtpUserIdJavaMail2 = input.trim();
		configSmtpUserPwJavaMail2();
	}

	private void configSmtpUserPwJavaMail2() {
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_SMTP_PASSWORD, null);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			configSmtpUserIdJavaMail2();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			configSmtpUserPwJavaMail2();
			return;
		}
		smtpUserPwJavaMail2 = input.trim();
		decideToEncryptSSLJavaMail2();
	}

	private void decideToEncryptSSLJavaMail2() {
		String javaMailEncrypt = TextCustomPanelUtils.showYorN(
				Messages.Notification_SMTP_ENCRYPT, enableSSLJavaMail2 ? 0 : 1,
				new String[] { Messages.PREVIOUS_INPUT_INDEX });
		if (javaMailEncrypt.trim().toUpperCase()
				.equals(Messages.PREVIOUS_INPUT_INDEX)) {
			if (enableAuthJavaMail2)
				configSmtpUserPwJavaMail2();
			else
				decideToAuthSmtpJavaMail2();
			return;
		} else
			enableSSLJavaMail2 = javaMailEncrypt.equals("Y") ? true : false;
		TextCustomPanelUtils.logInput("encrypt SSL java mail", javaMailEncrypt);
		configSMTPPort2(0);
	}

	private void configDNSInfo1() {
		TextCustomPanelUtils.showSubTitle2(Messages.Notification_DNS_MX);
		TextCustomPanelUtils.showText(Messages.Notification_DNS_MSG);
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_DNS_DOMAIN_NAME, smtpDomaiNameDNS1);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			startConfiguration();
			return;
		}
		TextCustomPanelUtils.logInput("domain name DNS", input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.warning_host_invalid);
			log.error("Host name is invalid");
			configDNSInfo1();
			return;
		}
		smtpDomaiNameDNS1 = input.trim();
		decideToChooseDNSServer1();
	}

	private void decideToChooseDNSServer1() {
		String input = TextCustomPanelUtils.showYorN(
				Messages.Notification_SMTP_SPECIFY_DNS_SERVER_INFO,
				enableServerInfoDNS1 ? 0 : 1,
				new String[] { Messages.PREVIOUS_INPUT_INDEX });
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			configDNSInfo1();
			return;
		}
		enableServerInfoDNS1 = input.equals("Y") ? true : false;
		TextCustomPanelUtils.logInput("enable server info DNS", input);
		if (enableServerInfoDNS1)
			configDNSServer1();
		else
			decideToAuthSmtpDNS1();
	}

	private void configDNSServer1() {
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_DNS_SERVER, dnsServerHost1);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			decideToChooseDNSServer1();
			return;
		}
		TextCustomPanelUtils.logInput("host name DNS", input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_DNS_INVALID_HOSTNAME_INPUT_WARNING);
			log.error("CLFRP0063E: Invalid host name of DNS server.");
			configDNSServer1();
			return;
		}
		dnsServerHost1 = input.trim();
		configDNSPort1();
	}

	private void configDNSPort1() {
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_DNS_PORT, dnsServerPort1);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			configDNSServer1();
			return;
		}
		TextCustomPanelUtils.logInput("server port DNS", input);
		if (!verifyPortComplete(input.trim())) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_DNS_INVALID_PORT_INPUT_WARNING);
			log.error("CLFRP0064E: Invalid port number of DNS server.");
			configDNSPort1();
			return;
		}
		dnsServerPort1 = input.trim();
		decideToAuthSmtpDNS1();
	}

	private void decideToAuthSmtpDNS1() {
		String dnsAuth = TextCustomPanelUtils.showYorN(
				Messages.Notification_SMTP_AUTH_INFO, enableAuthDNS1 ? 0 : 1,
				new String[] { Messages.PREVIOUS_INPUT_INDEX });
		if (dnsAuth.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			decideToChooseDNSServer1();
			return;
		} else
			enableAuthDNS1 = dnsAuth.equals("Y") ? true : false;
		TextCustomPanelUtils.logInput("enable authentication DNS", dnsAuth);
		if (enableAuthDNS1) {
			configSmtpUserIdDNS1();
		} else
			decideToEncryptSSLDNS1();
	}

	private void configSmtpUserIdDNS1() {
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_SMTP_USER_ID, smtpUserIdDNS1);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			decideToAuthSmtpDNS1();
			return;
		}
		TextCustomPanelUtils.logInput("smtp user id DNS", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_DNS_INVALID_SMTP_USERID_INPUT_WARNING);
			log.error("CLFRP0065E: Invalid user ID of SMTP messaging server.");
			configSmtpUserIdDNS1();
			return;
		}
		smtpUserIdDNS1 = input.trim();
		configSmtpUserPwDNS1();
	}

	private void configSmtpUserPwDNS1() {
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_SMTP_PASSWORD, null);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			configSmtpUserIdDNS1();
			return;
		}
		if (!verifyPasswordComplete(input.trim())) {
			configSmtpUserPwDNS1();
			return;
		}
		smtpUserPwDNS1 = input.trim();
		decideToEncryptSSLDNS1();
	}

	private void decideToEncryptSSLDNS1() {
		String dnsEncrypt = TextCustomPanelUtils.showYorN(
				Messages.Notification_SMTP_ENCRYPT, enableSSLDNS1 ? 0 : 1,
				new String[] { Messages.PREVIOUS_INPUT_INDEX });
		if (dnsEncrypt.trim().toUpperCase()
				.equals(Messages.PREVIOUS_INPUT_INDEX)) {
			if (enableSSLDNS1)
				configSmtpUserPwDNS1();
			else
				decideToAuthSmtpDNS1();
			return;
		} else
			enableSSLDNS1 = dnsEncrypt.equals("Y") ? true : false;
		TextCustomPanelUtils.logInput("encrypt SSL DNS", dnsEncrypt);
		configSMTPPort1(1);
	}

	private void configDNSServer2() {
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_DNS_SERVER, dnsServerHost2);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			decideToChooseDNSServer2();
			return;
		}
		TextCustomPanelUtils.logInput("server host name DNS", input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_DNS_INVALID_HOSTNAME_INPUT_WARNING);
			log.error("CLFRP0063E: Invalid host name of DNS server.");
			configDNSServer2();
			return;
		}
		dnsServerHost2 = input.trim();
		configDNSPort2();
	}

	private void configDNSPort2() {
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_DNS_PORT, dnsServerPort2);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			configDNSServer2();
			return;
		}
		TextCustomPanelUtils.logInput("DNS port", input);
		if (!verifyPortComplete(input.trim())) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_DNS_INVALID_PORT_INPUT_WARNING);
			log.error("CLFRP0064E: Invalid port number of DNS server.");
			configDNSPort2();
			return;
		}
		dnsServerPort2 = input.trim();
		decideToAuthSmtpDNS2();
	}

	private void configDNSInfo2() {
		TextCustomPanelUtils.showSubTitle2(Messages.Notification_DNS_MX);
		TextCustomPanelUtils.showText(Messages.Notification_DNS_MSG);
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_DNS_DOMAIN_NAME, smtpDomaiNameDNS2);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			startConfiguration();
			return;
		}
		TextCustomPanelUtils.logInput("smtp domain name DNS", input);
		if (!verifyHostNameComplete(input.trim())) {
			TextCustomPanelUtils.showError(Messages.warning_host_invalid);
			log.error("Host name is invalid");
			configDNSInfo2();
			return;
		}
		smtpDomaiNameDNS2 = input.trim();
		decideToChooseDNSServer2();
	}

	private void decideToChooseDNSServer2() {
		String input = TextCustomPanelUtils.showYorN(
				Messages.Notification_SMTP_SPECIFY_DNS_SERVER_INFO,
				enableServerInfoDNS2 ? 0 : 1,
				new String[] { Messages.PREVIOUS_INPUT_INDEX });
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			configDNSInfo2();
			return;
		}
		enableServerInfoDNS2 = input.equals("Y") ? true : false;
		TextCustomPanelUtils.logInput("specify dns server info DNS", input);
		if (enableServerInfoDNS2)
			configDNSServer2();
		else
			decideToAuthSmtpDNS2();
	}

	private void decideToAuthSmtpDNS2() {
		String dnsAuth = TextCustomPanelUtils.showYorN(
				Messages.Notification_SMTP_AUTH_INFO, enableAuthDNS2 ? 0 : 1,
				new String[] { Messages.PREVIOUS_INPUT_INDEX });
		if (dnsAuth.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			decideToChooseDNSServer2();
			return;
		} else
			enableAuthDNS2 = dnsAuth.equals("Y") ? true : false;
		TextCustomPanelUtils
				.logInput("enable smtp authentication DNS", dnsAuth);
		if (enableAuthDNS2) {
			configSmtpUserIdDNS2();
		} else
			decideToEncryptSSLDNS2();
	}

	private void configSmtpUserIdDNS2() {
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_SMTP_USER_ID, smtpUserIdDNS2);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			decideToAuthSmtpDNS2();
			return;
		}
		TextCustomPanelUtils.logInput("smtp user id DNS", input);
		if (!verifyUserNameComplete(input.trim())) {
			TextCustomPanelUtils
					.showError(Messages.Notificaton_DNS_INVALID_SMTP_USERID_INPUT_WARNING);
			log.error("CLFRP0065E: Invalid user ID of SMTP messaging server.");
			configSmtpUserIdDNS2();
			return;
		}
		smtpUserIdDNS2 = input.trim();
		configSmtpUserPwDNS2();
	}

	private void configSmtpUserPwDNS2() {
		String input = TextCustomPanelUtils.getInput(
				Messages.Notification_SMTP_PASSWORD, null);
		if (input.trim().equalsIgnoreCase(Messages.PREVIOUS_INPUT_INDEX)) {
			configSmtpUserIdDNS2();
			return;
		}

		if (!verifyPasswordComplete(input.trim())) {
			configSmtpUserPwDNS2();
			return;
		}
		smtpUserPwDNS2 = input.trim();
		decideToEncryptSSLDNS2();
	}

	private void decideToEncryptSSLDNS2() {
		String dnsEncrypt = TextCustomPanelUtils.showYorN(
				Messages.Notification_SMTP_ENCRYPT, enableSSLDNS2 ? 0 : 1,
				new String[] { Messages.PREVIOUS_INPUT_INDEX });
		if (dnsEncrypt.trim().toUpperCase()
				.equals(Messages.PREVIOUS_INPUT_INDEX)) {
			if (enableSSLDNS2)
				configSmtpUserPwDNS2();
			else
				decideToAuthSmtpDNS2();
			return;
		} else
			enableSSLDNS2 = dnsEncrypt.equals("Y") ? true : false;
		TextCustomPanelUtils.logInput("smtp encrypt SSL DNS", dnsEncrypt);
		configSMTPPort2(1);
	}

	private boolean verifyHostNameComplete(String hostName) {
		InstallValidator installvalidator = new InstallValidator();
		try {
			if (!installvalidator
					.hostNameValidateForNotificationPanel(hostName))
				return false;
		} catch (Exception e) {
			log.error(e);
			return false;
		}
		return true;
	}

	private boolean verifyPortComplete(String dmport) {
		InstallValidator installvalidator = new InstallValidator();
		try {
			if (!installvalidator.portNumValidate(dmport))
				return false;
		} catch (Exception e) {
			log.error(e);
			return false;
		}
		return true;
	}

	private boolean verifyPrefixComplete(String prefix) {
		boolean isValid = false;
		try {
			isValid = installValidator.validateEmailAddress1(prefix + "aaa");
		} catch (Exception e) {
			return false;
		}
		return isValid;
	}

	private boolean verifySuffixComplete(String suffix) {
		boolean isValid = false;
		try {
			isValid = installValidator.validateEmailAddress1("aaa" + suffix);
		} catch (Exception e) {
			return false;
		}
		return isValid;
	}

	private boolean verifyUserNameComplete(String dmuserId) {
		if (dmuserId == null || dmuserId.length() == 0)
			return false;
		InstallValidator iv = new InstallValidator();
		return !iv.containsSpace(dmuserId);
	}

	private boolean verifyPasswordComplete(String dmuserpw) {
		if (dmuserpw == null || dmuserpw.length() == 0) {
			TextCustomPanelUtils.showError(Messages.warning_password_empty);
			log.error("Password is required");
			return false;
		}
		InstallValidator iv = new InstallValidator();
		if (iv.containsInvalidPassword(dmuserpw) || iv.containsSpace(dmuserpw)) {
			TextCustomPanelUtils.showError(Messages.warning_password_invalid);
			log.error("Username and password contains invalid characters");
			return false;
		}
		return true;
	}

	private void setUserDataNone() {
		profile = getProfile();
		TextCustomPanelUtils.logUserData("user.notification.replyto.enabled",
				"false");
		TextCustomPanelUtils
				.logUserData("user.notification.replyto.domain", "");
		TextCustomPanelUtils.logUserData(
				"user.notification.replyto.localPart.type", "");
		TextCustomPanelUtils.logUserData("user.notification.replyto.localPart",
				"");
		TextCustomPanelUtils.logUserData(
				"user.notification.replyto.mailFile.hostName", "");
		TextCustomPanelUtils.logUserData(
				"user.notification.replyto.mailFile.userID", "");

		profile.setUserData("user.notification.replyto.enabled", "false");
		profile.setUserData("user.notification.replyto.domain", "");
		profile.setUserData("user.notification.replyto.localPart.type", "");
		profile.setUserData("user.notification.replyto.localPart", "");
		profile.setUserData("user.notification.replyto.mailFile.hostName", "");
		profile.setUserData("user.notification.replyto.mailFile.userID", "");
		profile.setUserData("user.notification.replyto.mailFile.password", "");
		
		profile.setUserData("user.notification.replyto.info", "");
		
	}

	public void setUserDataJavaMail(String host, String port, String userId,
			String pwd, boolean isSSL) {
		profile = getProfile();

		TextCustomPanelUtils.logUserData("user.notification.enabled", "true");
		TextCustomPanelUtils.logUserData(
				"user.notification.useJavaMailProvider", "true");
		TextCustomPanelUtils.logUserData("user.notification.host", host);
		TextCustomPanelUtils.logUserData("user.notification.port", port);
		TextCustomPanelUtils.logUserData("user.notification.user", userId);
		TextCustomPanelUtils.logUserData("user.notification.ssl.enabled", isSSL
				+ "");
		TextCustomPanelUtils.logUserData("user.notification.dnshost", "");
		TextCustomPanelUtils.logUserData("user.notification.dnsport", "");
		TextCustomPanelUtils.logUserData("user.notification.domain", "");

		profile.setUserData("user.notification.enabled", "true");
		profile.setUserData("user.notification.useJavaMailProvider", "true");
		profile.setUserData("user.notification.host", host);
		profile.setUserData("user.notification.port", port);
		profile.setUserData("user.notification.user", userId);
		profile.setUserData("user.notification.password", pwd);
		profile.setUserData("user.notification.ssl.enabled", isSSL + "");
		profile.setUserData("user.notification.dnshost", "");
		profile.setUserData("user.notification.dnsport", "");
		profile.setUserData("user.notification.domain", "");
	}

	private void setUserDataDNS(String dnsHost, String dnsPort, String domain,
			String port, String userId, String pwd, boolean isSSL) {
		profile = getProfile();

		TextCustomPanelUtils.logUserData("user.notification.enabled", "true");
		TextCustomPanelUtils.logUserData(
				"user.notification.useJavaMailProvider", "false");
		TextCustomPanelUtils.logUserData("user.notification.host", "");
		TextCustomPanelUtils.logUserData("user.notification.port", port);
		TextCustomPanelUtils.logUserData("user.notification.user", userId);
		TextCustomPanelUtils.logUserData("user.notification.ssl.enabled", isSSL
				+ "");
		TextCustomPanelUtils.logUserData("user.notification.dnshost", dnsHost);
		TextCustomPanelUtils.logUserData("user.notification.dnsport", dnsPort);
		TextCustomPanelUtils.logUserData("user.notification.domain", domain);

		profile.setUserData("user.notification.enabled", "true");
		profile.setUserData("user.notification.useJavaMailProvider", "false");
		profile.setUserData("user.notification.host", "");
		profile.setUserData("user.notification.port", port);
		profile.setUserData("user.notification.user", userId);
		profile.setUserData("user.notification.password", pwd);
		profile.setUserData("user.notification.ssl.enabled", isSSL + "");
		profile.setUserData("user.notification.dnshost", dnsHost);
		profile.setUserData("user.notification.dnsport", dnsPort);
		profile.setUserData("user.notification.domain", domain);
	}

	private void setUserDataReplyTo(String domain, String type,
			String localPart, String hostName, String userId, String pwd) {
		profile = getProfile();

		TextCustomPanelUtils.logUserData("user.notification.replyto.enabled",
				"true");
		TextCustomPanelUtils.logUserData("user.notification.replyto.domain",
				domain);
		TextCustomPanelUtils.logUserData(
				"user.notification.replyto.localPart.type", type);
		TextCustomPanelUtils.logUserData("user.notification.replyto.localPart",
				localPart);
		TextCustomPanelUtils.logUserData(
				"user.notification.replyto.mailFile.hostName", hostName);
		TextCustomPanelUtils.logUserData(
				"user.notification.replyto.mailFile.userID", userId);

		profile.setUserData("user.notification.replyto.enabled", "true");
		profile.setUserData("user.notification.replyto.domain", domain);
		profile.setUserData("user.notification.replyto.localPart.type", type);
		profile.setUserData("user.notification.replyto.localPart", localPart);
		profile.setUserData("user.notification.replyto.mailFile.hostName",
				hostName);
		profile.setUserData("user.notification.replyto.mailFile.userID", userId);
		profile.setUserData("user.notification.replyto.mailFile.password", pwd);
		
		StringBuffer replyto = new StringBuffer();
		replyto.append("\"replyto\":{").append("\n");
		replyto.append("\"domain\": \"").append(profile.getUserData("user.notification.replyto.domain")).append("\",").append("\n");
		replyto.append("\"suffix\": \"").append(profile.getUserData("user.notification.replyto.suffix")==null?"":profile.getUserData("user.notification.replyto.suffix")).append("\",").append("\n");
		replyto.append("\"prefix\": \"").append(profile.getUserData("user.notification.replyto.prefix")==null?"":profile.getUserData("user.notification.replyto.prefix")).append("\",").append("\n");
		replyto.append("\"store\":{").append("\n");
		replyto.append("\"mailStoreHost\": \"").append(profile.getUserData("user.notification.replyto.mailFile.hostName")).append("\",").append("\n");
		replyto.append("\"mailStoreUser\": \"").append(profile.getUserData("user.notification.replyto.mailFile.userID")).append("\",").append("\n");
		replyto.append("\"mailStorePassword\": \"").append(profile.getUserData("user.notification.replyto.mailFile.password")).append("\",").append("\n");
		replyto.append("\"mailStoreProtocol\": \"imap\"}").append("\n");
		replyto.append(" },").append("\n");
		
		profile.setUserData("user.notification.replyto.info", replyto.toString());
		
	}

	@Override
	public String getFeatureId() {
		return Constants.FEATURE_ID_NOTIFICATION_PANEL;
	}

}
