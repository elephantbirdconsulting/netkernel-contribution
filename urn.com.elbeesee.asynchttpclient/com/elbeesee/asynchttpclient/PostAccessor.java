package com.elbeesee.asynchttpclient;

import org.netkernel.layer0.nkf.*;
import org.netkernel.layer0.meta.impl.SourcedArgumentMetaImpl;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

import org.netkernel.layer0.nkf.impl.*;
import org.netkernel.layer0.representation.ByteArrayRepresentation;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.request.IResponse;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;

public class PostAccessor extends StandardAccessorImpl {
	private static CloseableHttpAsyncClient mHttpclient;
	
	public PostAccessor() {
		this.declareThreadSafe();
		this.declareArgument(new SourcedArgumentMetaImpl("url",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("nvp",null,null,new Class[] {IHDSNode.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("headers",null,null,new Class[] {IHDSNode.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("credentials",null,null,new Class[] {IHDSNode.class}));
	}
	
	public void postCommission(INKFRequestContext aContext) throws Exception {
		mHttpclient = HttpAsyncClients.createDefault();
		mHttpclient.start();
	}
	
	public void preDecommission(INKFRequestContext aContext) throws Exception {
		mHttpclient.close();
	}
	
	public void onSource(INKFRequestContext aContext) throws Exception {
		// url
		String aURL = null;
		try {
			aURL = aContext.source("arg:url", String.class);
		}
		catch (Exception e){
			throw new Exception("the request does not have a valid - url - argument");
		}
		if (aURL.equals("")) {
			throw new Exception("the request does not have a valid - url - argument");
		}
		HttpPost httppostrequest = null;
		try {
			httppostrequest = new HttpPost(aURL);
		}
		catch (Exception e) {
			throw new Exception("the request does not have a valid - url - argument");
		}
		//
		
		// nvp
		IHDSNode aNVP = null;
		if (aContext.getThisRequest().argumentExists("nvp")) {
			try {
				aNVP = aContext.source("arg:nvp", IHDSNode.class);
				List<NameValuePair> vPairs = new ArrayList<NameValuePair>();
				for (IHDSNode vPair : aNVP.getChildren()) {
					vPairs.add(new BasicNameValuePair(vPair.getName(),(String)vPair.getValue()));
				}
				httppostrequest.setEntity(new UrlEncodedFormEntity(vPairs, "UTF-8"));
			}
			catch (Exception e) {
				throw new Exception("the request does not have a valid - nvp - argument");
			}
		}
		//
		
		// headers
		IHDSNode aHeaders = null;
		if (aContext.getThisRequest().argumentExists("headers")) {
			try {
				aHeaders = aContext.source("arg:headers", IHDSNode.class);
				for (IHDSNode vHeader : aHeaders.getChildren()) {
					httppostrequest.addHeader(vHeader.getName(),(String)vHeader.getValue());
				}
			}
			catch (Exception e) {
				throw new Exception("the request does not have a valid - headers - argument");				
			}
		}
		//
		
		// credentials
		IHDSNode aCredentials = null;
		HttpClientContext httppostcontext = HttpClientContext.create();
		if (aContext.getThisRequest().argumentExists("credentials")) {
			try {
				aCredentials = aContext.source("arg:credentials", IHDSNode.class);
		        String vHost = (String)aCredentials.getFirstValue("/httpCredentials/host");
		        String vPort = (String)aCredentials.getFirstValue("/httpCredentials/port");
		        String vUsername = (String)aCredentials.getFirstValue("/httpCredentials/username");
		        String vPassword = (String)aCredentials.getFirstValue("/httpCredentials/password");
		        int vPortInt = Integer.parseInt(vPort);
		        
		        CredentialsProvider vCredentialsProvider = new BasicCredentialsProvider();
				vCredentialsProvider.setCredentials(
		                new AuthScope(vHost, vPortInt),
		                new UsernamePasswordCredentials(vUsername, vPassword));
				httppostcontext.setCredentialsProvider(vCredentialsProvider);
			}
			catch (Exception e) {
				throw new Exception("the request does not have a valid - credentials - argument");				
			}
		}
		//
		
		mHttpclient.execute(httppostrequest, httppostcontext, new FutureCallbackImpl(aContext));
		
		aContext.setNoResponse();
	}
	
	class FutureCallbackImpl implements FutureCallback<HttpResponse> {
		private INKFRequestContext mContext;
		
		public FutureCallbackImpl(INKFRequestContext aContext) {	
			mContext = aContext;
		}

		@Override
		public void cancelled() {	
			NKFException e = new NKFException("HTTP Request Cancelled");
			INKFResponse vResponse = mContext.createResponseFrom(e);
			IResponse vKernelResponse = ((NKFResponseImpl)vResponse).getKernelResponse();
			((NKFContextImpl)mContext).handleAsyncResponse(vKernelResponse);
		}

		@Override
		public void completed(HttpResponse response) {	
			try {
				INKFResponse vResponse = mContext.createResponseFrom(getResponse(response));
				ContentType contentType = ContentType.get(response.getEntity());
				String vMimeType = contentType.getMimeType();
				vResponse.setMimeType(vMimeType);
				IResponse vKernelResponse = ((NKFResponseImpl)vResponse).getKernelResponse();
				((NKFContextImpl)mContext).handleAsyncResponse(vKernelResponse);
			}
			catch(Exception e) {
				INKFResponse vResponse = mContext.createResponseFrom(e);
				IResponse vKernelResponse = ((NKFResponseImpl)vResponse).getKernelResponse();
				((NKFContextImpl)mContext).handleAsyncResponse(vKernelResponse);
			}
		}

		@Override
		public void failed(Exception e) {	
			INKFResponse vResponse = mContext.createResponseFrom(e);
			IResponse vKernelResponse = ((NKFResponseImpl)vResponse).getKernelResponse();
			((NKFContextImpl)mContext).handleAsyncResponse(vKernelResponse);
		}
	}
	
	private ByteArrayRepresentation getResponse(HttpResponse response) throws Exception {	
		return new ByteArrayRepresentation(getResponseInner(response).toByteArray());
	}
	
	private ByteArrayOutputStream getResponseInner(HttpResponse response) throws Exception {
		long size;
		HttpEntity e=response.getEntity();
		InputStream bytes;
		if(e!=null)
		{
			size=e.getContentLength();
			if(size<=0)
			{	size=2048;			
			}
			bytes=e.getContent();
		}
		else
		{	byte[]empty=new byte[0];
			bytes=new ByteArrayInputStream(empty);
			size=1;
		}
		try
		{
			ByteArrayOutputStream baos=new ByteArrayOutputStream((int)size);
			org.netkernel.layer0.util.Utils.pipe(
				bytes,
				new BufferedOutputStream(baos)
			);
			return baos;
		}
		catch(IOException ex)
		{	throw ex;
		}
		catch(RuntimeException re)
		{	bytes.close();
			throw re;
		}
	}

}
