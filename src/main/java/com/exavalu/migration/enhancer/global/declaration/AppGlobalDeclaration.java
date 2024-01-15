package com.exavalu.migration.enhancer.global.declaration;

public class AppGlobalDeclaration {

	private static boolean mule3ApikitExceptionToErrorHandler;
	private static boolean idempotentXMLModifier;
	private static boolean roundrobinXMlModifier;
	private static boolean inboundOutboundProperties;
	private static boolean configureManagedStore;
	private static boolean configureObjectStore;
	private static boolean propertyModifier;
	private static boolean setSessionVariableToSetVariable;
	private static boolean validationXMLModifier;
	private static boolean vmqueue;
	private static boolean xmlCommentRemover;

	public static boolean isMule3ApikitExceptionToErrorHandler() {
		return mule3ApikitExceptionToErrorHandler;
	}

	public static void setMule3ApikitExceptionToErrorHandler(boolean mule3ApikitExceptionToErrorHandler) {
		AppGlobalDeclaration.mule3ApikitExceptionToErrorHandler = mule3ApikitExceptionToErrorHandler;
	}

	public static boolean isIdempotentXMLModifier() {
		return idempotentXMLModifier;
	}

	public static void setIdempotentXMLModifier(boolean idempotentXMLModifier) {
		AppGlobalDeclaration.idempotentXMLModifier = idempotentXMLModifier;
	}

	public static boolean isRoundrobinXMlModifier() {
		return roundrobinXMlModifier;
	}

	public static void setRoundrobinXMlModifier(boolean roundrobinXMlModifier) {
		AppGlobalDeclaration.roundrobinXMlModifier = roundrobinXMlModifier;
	}

	public static boolean isInboundOutboundProperties() {
		return inboundOutboundProperties;
	}

	public static void setInboundOutboundProperties(boolean inboundOutboundProperties) {
		AppGlobalDeclaration.inboundOutboundProperties = inboundOutboundProperties;
	}

	public static boolean isConfigureManagedStore() {
		return configureManagedStore;
	}

	public static void setConfigureManagedStore(boolean configureManagedStore) {
		AppGlobalDeclaration.configureManagedStore = configureManagedStore;
	}

	public static boolean isConfigureObjectStore() {
		return configureObjectStore;
	}

	public static void setConfigureObjectStore(boolean configureObjectStore) {
		AppGlobalDeclaration.configureObjectStore = configureObjectStore;
	}

	public static boolean isPropertyModifier() {
		return propertyModifier;
	}

	public static void setPropertyModifier(boolean propertyModifier) {
		AppGlobalDeclaration.propertyModifier = propertyModifier;
	}

	public static boolean isSetSessionVariableToSetVariable() {
		return setSessionVariableToSetVariable;
	}

	public static void setSetSessionVariableToSetVariable(boolean setSessionVariableToSetVariable) {
		AppGlobalDeclaration.setSessionVariableToSetVariable = setSessionVariableToSetVariable;
	}

	public static boolean isValidationXMLModifier() {
		return validationXMLModifier;
	}

	public static void setValidationXMLModifier(boolean validationXMLModifier) {
		AppGlobalDeclaration.validationXMLModifier = validationXMLModifier;
	}

	public static boolean isVmqueue() {
		return vmqueue;
	}

	public static void setVmqueue(boolean vmqueue) {
		AppGlobalDeclaration.vmqueue = vmqueue;
	}

	public static boolean isXmlCommentRemover() {
		return xmlCommentRemover;
	}

	public static void setXmlCommentRemover(boolean xmlCommentRemover) {
		AppGlobalDeclaration.xmlCommentRemover = xmlCommentRemover;
	}

}
