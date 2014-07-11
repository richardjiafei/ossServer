package oss.server.common;

public interface OssServeType {

	//NODE_TYPE
	final static char NODE_TYPE_CLIENT=0x1;
	final static char NODE_TYPE_SERVER=0x02;
	
	
	//OSS_TYPE
	final static char OSS_TYPE_RESERVED = 0x00;
	final static char OSS_TYPE_ATTACH_REQ = 0x01;
	final static char OSS_TYPE_ATTACH_RESP = 0x02;
	final static char OSS_TYPE_DATA = 0x23;
	
	final static int MAX_CLIENT_NUM=100;
}
