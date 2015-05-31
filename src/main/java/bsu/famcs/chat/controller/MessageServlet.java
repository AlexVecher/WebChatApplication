package bsu.famcs.chat.controller;

import bsu.famcs.chat.model.Message;
import bsu.famcs.chat.model.MessageStorage;
import bsu.famcs.chat.storage.xml.XMLHistoryUtil;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.PrintWriter;

import static bsu.famcs.chat.util.MessageUtil.*;
import static bsu.famcs.chat.util.ServletUtil.APPLICATION_JSON;
import static bsu.famcs.chat.util.ServletUtil.getMessageBody;

@WebServlet("/chat")
public class MessageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private int isModifiedStorage = 0;
    private static Logger logger = Logger.getLogger(MessageServlet.class.getName());

    @Override
    public void init() throws ServletException {
        try {
            loadHistory();
        } catch (TransformerException e) {
            logger.error(e);
        } catch (ParserConfigurationException e) {
            logger.error(e);
        } catch (SAXException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter(TOKEN);
        logger.info("Get request");
        if (token != null && !"".equals(token)) {
            int index = getIndex(token);
            if(isModifiedStorage == index && isModifiedStorage != 0) {
                logger.info("GET request: response status: 304 Not Modified");
                response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            } else {
                String messages = serverResponse(0);
                try {
                    JSONObject json = stringToJson(messages);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                response.setContentType(APPLICATION_JSON);
                PrintWriter out = response.getWriter();
                out.print(messages);
                out.flush();
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "token parameter is absent");
            logger.error("Token parameter is absent");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Post request");
        String data = getMessageBody(request);
        logger.info("Request data : " + data);
        try {
            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            System.out.println(message.getUserMessage() );
            logger.info(message.getUserMessage());
            XMLHistoryUtil.addMessage(message);
            MessageStorage.addMessagePost(message);
            isModifiedStorage++;
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Invalid message1");
        } catch (TransformerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Invalid message2");
        } catch (SAXException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Invalid message3");
        } catch (ParserConfigurationException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Invalid message4");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Delete request");
        String data = getMessageBody(request);
        logger.info("Request data : " + data);
        Message message;
        try {
            JSONObject json = stringToJson(data);
            message = jsonToCurrentMessage(json);
            message.isDelete();
            Message updated = XMLHistoryUtil.updateMessage(message);
            MessageStorage.addMessageDelete(updated);
            isModifiedStorage++;
        } catch (XPathExpressionException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Invalid message");
        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Invalid message");
        } catch (TransformerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Invalid message");
        } catch (SAXException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Invalid message");
        } catch (ParserConfigurationException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Invalid message");
        } catch (NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Invalid message");
        }
    }

    @SuppressWarnings("unchecked")
    private String serverResponse(int index) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MESSAGES, MessageStorage.getSubHistory(index));
        jsonObject.put(TOKEN, getToken(isModifiedStorage));
        return jsonObject.toJSONString();
    }


    private void loadHistory() throws TransformerException, ParserConfigurationException, IOException,
            SAXException {
        if (!XMLHistoryUtil.isStorageExist()) {
            XMLHistoryUtil.createStorage();
            logger.info(MessageStorage.getSubHistory(0));
        } else {
            MessageStorage.addAll(XMLHistoryUtil.getMessages());
            logger.info('\n' + MessageStorage.getStringView());
            logger.info(MessageStorage.getSubHistory(0));
        }
    }
}