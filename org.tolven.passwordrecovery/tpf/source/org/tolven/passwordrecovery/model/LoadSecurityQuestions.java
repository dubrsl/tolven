package org.tolven.passwordrecovery.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.tolven.passwordrecovery.bean.ObjectFactory;
import org.tolven.passwordrecovery.bean.Questions;
import org.tolven.passwordrecovery.bean.SecurityQuestionDetail;
import org.tolven.restful.client.RESTfulClient;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class LoadSecurityQuestions extends RESTfulClient {

    public static final String QUESTIONS_JAXB_PACKAGE = "org.tolven.passwordrecovery.bean";

    public LoadSecurityQuestions(String userId, char[] password, String appRestfulURL, String authRestfulURL) {
        init(userId, password, appRestfulURL, authRestfulURL);
    }

    public void displaySecurityQuestions() {
        List<SecurityQuestion> securityQuestions = getSecurityQuestions(SecurityQuestionPurpose.LOGIN_PASSWORD_RECOVERY.value());
        Comparator<SecurityQuestion> comparator = new Comparator<SecurityQuestion>() {
            public int compare(SecurityQuestion securityQuestion1, SecurityQuestion securityQuestion2) {
                return (securityQuestion1.getQuestion().compareTo(securityQuestion2.getQuestion()));
            };
        };
        Collections.sort(securityQuestions, comparator);
        for (SecurityQuestion securityQuestion : securityQuestions) {
            System.out.println(securityQuestion.getQuestion());
        }
    }

    private List<SecurityQuestion> getSecurityQuestions(String purpose) {
        return getSecurityQuestions(null, purpose);
    }

    private List<SecurityQuestion> getSecurityQuestions(String question, String purpose) {
        WebResource webResource = getAppWebResource().path("passwordRecovery/getSecurityQuestions");
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        if (question != null) {
            formData.putSingle("question", question);
        }
        if (purpose != null) {
            formData.putSingle("purpose", purpose);
        }
        ClientResponse response = webResource.cookie(getTokenCookie()).post(ClientResponse.class, formData);
        if (response.getStatus() >= 400) {
            throw new RuntimeException("Could not get security questions" + "Error " + response.getStatus());
        }
        MultivaluedMap<String, String> resultsMap = response.getEntity(MultivaluedMap.class);
        List<SecurityQuestion> securityQuestions = new ArrayList<SecurityQuestion>();
        for (String name : resultsMap.keySet()) {
            SecurityQuestion securityQuestion = new SecurityQuestion(name, resultsMap.getFirst(name));
            securityQuestions.add(securityQuestion);
        }
        return securityQuestions;
    }

    public void addSecurityQuestion(String question) {
        SecurityQuestion securityQuestion = new SecurityQuestion(question, SecurityQuestionPurpose.LOGIN_PASSWORD_RECOVERY.value());
        List<SecurityQuestion> securityQuestions = new ArrayList<SecurityQuestion>();
        securityQuestions.add(securityQuestion);
        addSecurityQuestions(securityQuestions);
    }

    public void addSecurityQuestions(List<SecurityQuestion> securityQuestions) {
        WebResource webResource = getAppWebResource().path("passwordRecovery/addSecurityQuestions");
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        for (SecurityQuestion securityQuestion : securityQuestions) {
            formData.putSingle(securityQuestion.getQuestion(), securityQuestion.getPurpose());
        }
        ClientResponse response = webResource.cookie(getTokenCookie()).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
        if (response.getStatus() >= 400) {
            throw new RuntimeException("Could not add security questions " + "Error " + response.getStatus());
        }
    }

    public void changeSecurityQuestion(String currentSecurityQuestion, String newSecurityQuestion) {
        WebResource webResource = getAppWebResource().path("passwordRecovery/changeSecurityQuestion");
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.putSingle("currentSecurityQuestion", currentSecurityQuestion);
        formData.putSingle("newSecurityQuestion", newSecurityQuestion);
        formData.putSingle("purpose", SecurityQuestionPurpose.LOGIN_PASSWORD_RECOVERY.value());
        ClientResponse response = webResource.cookie(getTokenCookie()).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
        if (response.getStatus() >= 400) {
            throw new RuntimeException("Could not change security question " + "Error " + response.getStatus());
        }
    }

    public void removeSecurityQuestion(String question) {
        SecurityQuestion securityQuestion = new SecurityQuestion(question, SecurityQuestionPurpose.LOGIN_PASSWORD_RECOVERY.value());
        List<SecurityQuestion> securityQuestions = new ArrayList<SecurityQuestion>();
        securityQuestions.add(securityQuestion);
        removeSecurityQuestions(securityQuestions);
    }

    public void removeSecurityQuestions(List<SecurityQuestion> securityQuestions) {
        WebResource webResource = getAppWebResource().path("passwordRecovery/removeSecurityQuestions");
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        for (SecurityQuestion securityQuestion : securityQuestions) {
            formData.putSingle(securityQuestion.getQuestion(), securityQuestion.getPurpose());
        }
        ClientResponse response = webResource.cookie(getTokenCookie()).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
        if (response.getStatus() >= 400) {
            throw new RuntimeException("Could not remove security questions " + "Error " + response.getStatus());
        }
    }

    public PasswordRecoveryQuestionInfo addPasswordRecoveryQuestionInfo(String securityQuestionString) {
        SecurityQuestion securityQuestion = newSecurityQuestion(securityQuestionString, SecurityQuestionPurpose.LOGIN_PASSWORD_RECOVERY.value());
        PasswordRecoveryQuestionInfo passwordRecoveryQuestionInfo = new PasswordRecoveryQuestionInfo(securityQuestion);
        passwordRecoveryQuestionInfo.setStatus(PasswordRecoveryQuestionInfo.ADDPENDING);
        return passwordRecoveryQuestionInfo;
    }

    private List<SecurityQuestion> getDatabaseSecurityQuestions() {
        return getSecurityQuestions(SecurityQuestionPurpose.LOGIN_PASSWORD_RECOVERY.value());
    }

    private List<PasswordRecoveryQuestionInfo> getDatabasePasswordRecoveryQuestionInfos() {
        List<PasswordRecoveryQuestionInfo> passwordRecoveryQuestionInfos = new ArrayList<PasswordRecoveryQuestionInfo>();
        List<SecurityQuestion> securityQuestions = getDatabaseSecurityQuestions();
        PasswordRecoveryQuestionInfo passwordRecoveryQuestionInfo = null;
        for (SecurityQuestion securityQuestion : securityQuestions) {
            passwordRecoveryQuestionInfo = new PasswordRecoveryQuestionInfo(securityQuestion);
            passwordRecoveryQuestionInfos.add(passwordRecoveryQuestionInfo);
        }
        return passwordRecoveryQuestionInfos;
    }

    public List<PasswordRecoveryQuestionInfo> getPasswordRecoveryQuestionInfos() {
        List<PasswordRecoveryQuestionInfo> passwordRecoveryQuestionInfos = getDatabasePasswordRecoveryQuestionInfos();
        for (PasswordRecoveryQuestionInfo passwordRecoveryQuestionInfo : passwordRecoveryQuestionInfos) {
            passwordRecoveryQuestionInfo.setStatus(PasswordRecoveryQuestionInfo.UPTODATE);
        }
        Comparator<Object> comparator = new Comparator<Object>() {
            public int compare(Object obj1, Object obj2) {
                return ((PasswordRecoveryQuestionInfo) obj1).getQuestion().compareTo(((PasswordRecoveryQuestionInfo) obj2).getQuestion());
            };
        };
        Collections.sort(passwordRecoveryQuestionInfos, comparator);
        return passwordRecoveryQuestionInfos;
    }

    public void importSecurityQuestions(File securityQuestionFile) {
        List<File> securityQuestionFiles = new ArrayList<File>();
        securityQuestionFiles.add(securityQuestionFile);
        importSecurityQuestions(securityQuestionFiles);
    }

    private void importSecurityQuestions(List<File> securityQuestionFiles) {
        List<PasswordRecoveryQuestionInfo> passwordRecoveryQuestionInfos = importPasswordRecoveryQuestionInfos(securityQuestionFiles);
        writePasswordRecoveryQuestionInfos(passwordRecoveryQuestionInfos, new ArrayList<PasswordRecoveryQuestionInfo>());
    }

    public List<PasswordRecoveryQuestionInfo> importPasswordRecoveryQuestionInfos(File securityQuestionFile) {
        List<File> securityQuestionFiles = new ArrayList<File>();
        securityQuestionFiles.add(securityQuestionFile);
        return importPasswordRecoveryQuestionInfos(securityQuestionFiles);
    }

    private List<PasswordRecoveryQuestionInfo> importPasswordRecoveryQuestionInfos(List<File> securityQuestionFiles) {
        HashMap<String, PasswordRecoveryQuestionInfo> passwordRecoveryQuestionInfoMap = new HashMap<String, PasswordRecoveryQuestionInfo>();
        PasswordRecoveryQuestionInfo passwordRecoveryQuestionInfo = null;
        for (SecurityQuestion securityQuestion : getDatabaseSecurityQuestions()) {
            passwordRecoveryQuestionInfo = new PasswordRecoveryQuestionInfo(securityQuestion);
            passwordRecoveryQuestionInfo.setStatus(PasswordRecoveryQuestionInfo.UPTODATE);
            passwordRecoveryQuestionInfoMap.put(passwordRecoveryQuestionInfo.getPurposeAndQuestion(), passwordRecoveryQuestionInfo);
        }
        for (File securityQuestionFile : securityQuestionFiles) {
            List<SecurityQuestion> newSecurityQuestions = loadSecurityQuestions(securityQuestionFile);
            PasswordRecoveryQuestionInfo importedPasswordRecoveryQuestionInfo = null;
            for (SecurityQuestion securityQuestion : newSecurityQuestions) {
                importedPasswordRecoveryQuestionInfo = new PasswordRecoveryQuestionInfo(securityQuestion);
                if (passwordRecoveryQuestionInfoMap.get(importedPasswordRecoveryQuestionInfo.getPurposeAndQuestion()) == null) {
                    importedPasswordRecoveryQuestionInfo.setStatus(PasswordRecoveryQuestionInfo.ADDPENDING);
                    passwordRecoveryQuestionInfoMap.put(importedPasswordRecoveryQuestionInfo.getPurposeAndQuestion(), importedPasswordRecoveryQuestionInfo);
                }
            }
        }
        List<PasswordRecoveryQuestionInfo> passwordRecoveryQuestionInfos = new ArrayList<PasswordRecoveryQuestionInfo>(passwordRecoveryQuestionInfoMap.values());
        Comparator<Object> comparator = new Comparator<Object>() {
            public int compare(Object obj1, Object obj2) {
                return ((PasswordRecoveryQuestionInfo) obj1).getQuestion().compareTo(((PasswordRecoveryQuestionInfo) obj2).getQuestion());
            };
        };
        Collections.sort(passwordRecoveryQuestionInfos, comparator);
        return passwordRecoveryQuestionInfos;
    }

    private List<SecurityQuestion> loadSecurityQuestions(File questionsFile) {
        try {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(questionsFile.getPath());
                try {
                    JAXBContext jc = JAXBContext.newInstance(QUESTIONS_JAXB_PACKAGE, getClass().getClassLoader());
                    Unmarshaller u = jc.createUnmarshaller();
                    Questions questions = (Questions) u.unmarshal(fis);
                    List<SecurityQuestion> securityQuestions = new ArrayList<SecurityQuestion>();
                    SecurityQuestion securityQuestion = null;
                    for (SecurityQuestionDetail securityQuestionDetail : questions.getSecurityQuestion()) {
                        securityQuestion = newSecurityQuestion(securityQuestionDetail.getQuestion(), securityQuestionDetail.getPurpose());
                        securityQuestions.add(securityQuestion);
                    }
                    return securityQuestions;
                } catch (JAXBException ex) {
                    throw new RuntimeException("Could not load security questions from: " + questionsFile.getPath(), ex);
                }
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not load security questions from: " + questionsFile.getPath(), ex);
        }
    }

    private SecurityQuestion newSecurityQuestion(String question, String purpose) {
        return new SecurityQuestion(question, purpose);
    }

    public void exportPasswordRecoveryQuestionInfos(File questionsFile) {
        List<SecurityQuestion> databaseSecurityQuestions = getDatabaseSecurityQuestions();
        ObjectFactory objectFactory = new ObjectFactory();
        Questions questions = objectFactory.createQuestions();
        SecurityQuestionDetail securityQuestionDetail = null;
        for (SecurityQuestion securityQuestion : databaseSecurityQuestions) {
            securityQuestionDetail = objectFactory.createSecurityQuestionDetail();
            securityQuestionDetail.setQuestion(securityQuestion.getQuestion());
            securityQuestionDetail.setPurpose(securityQuestion.getPurpose());
            questions.getSecurityQuestion().add(securityQuestionDetail);
        }
        try {
            FileOutputStream out = null;
            try {
                JAXBContext jc = JAXBContext.newInstance(QUESTIONS_JAXB_PACKAGE, getClass().getClassLoader());
                Marshaller marshaller = jc.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                out = new FileOutputStream(questionsFile);
                marshaller.marshal(questions, out);
            } finally {
                if (out != null)
                    out.close();
            }
        } catch (JAXBException ex) {
            throw new RuntimeException("Could not store security questions to file: " + questionsFile.getPath(), ex);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store security questions to file: " + questionsFile.getPath(), ex);
        }
    }

    public void writePasswordRecoveryQuestionInfos(List<PasswordRecoveryQuestionInfo> passwordRecoveryQuestionInfos) {
        List<PasswordRecoveryQuestionInfo> passwordRecoveryQuestionsToDelete = new ArrayList<PasswordRecoveryQuestionInfo>();
        List<PasswordRecoveryQuestionInfo> passwordRecoveryQuestionInfosForUpdate = new ArrayList<PasswordRecoveryQuestionInfo>();
        for (PasswordRecoveryQuestionInfo passwordRecoveryQuestionInfo : passwordRecoveryQuestionInfos) {
            if (passwordRecoveryQuestionInfo.getStatus() == PasswordRecoveryQuestionInfo.REMOVEPENDING) {
                passwordRecoveryQuestionsToDelete.add(passwordRecoveryQuestionInfo);
            } else {
                passwordRecoveryQuestionInfosForUpdate.add(passwordRecoveryQuestionInfo);
            }
        }
        writePasswordRecoveryQuestionInfos(passwordRecoveryQuestionInfosForUpdate, passwordRecoveryQuestionsToDelete);
    }

    private void writePasswordRecoveryQuestionInfos(List<PasswordRecoveryQuestionInfo> passwordRecoveryQuestionInfosForUpdate, List<PasswordRecoveryQuestionInfo> passwordRecoveryQuestionsToDelete) {
        Map<String, SecurityQuestion> databaseSecurityQuestionsMap = new HashMap<String, SecurityQuestion>();
        List<SecurityQuestion> databaseSecurityQuestions = getDatabaseSecurityQuestions();
        for (SecurityQuestion securityQuestion : databaseSecurityQuestions) {
            databaseSecurityQuestionsMap.put(securityQuestion.getPurposeAndQuestion(), securityQuestion);
        }
        Map<String, PasswordRecoveryQuestionInfo> currentSecurityQuestionsMap = new HashMap<String, PasswordRecoveryQuestionInfo>();
        List<SecurityQuestion> securityQuestionsForUpdate = new ArrayList<SecurityQuestion>();
        SecurityQuestion databaseSecurityQuestion = null;
        for (PasswordRecoveryQuestionInfo currentPasswordRecoveryQuestionInfo : passwordRecoveryQuestionInfosForUpdate) {
            databaseSecurityQuestion = databaseSecurityQuestionsMap.get(currentPasswordRecoveryQuestionInfo.getPurposeAndQuestion());
            if (databaseSecurityQuestion == null) {
                currentSecurityQuestionsMap.put(currentPasswordRecoveryQuestionInfo.getPurposeAndQuestion(), currentPasswordRecoveryQuestionInfo);
                securityQuestionsForUpdate.add(currentPasswordRecoveryQuestionInfo.getSecurityQuestion());
            } else {
                currentPasswordRecoveryQuestionInfo.setSecurityQuestion(databaseSecurityQuestion);
                currentPasswordRecoveryQuestionInfo.setStatus(PasswordRecoveryQuestionInfo.UPTODATE);
            }
        }
        try {
            List<SecurityQuestion> securityQuestionsToDelete = new ArrayList<SecurityQuestion>();
            SecurityQuestion securityQuestionToDelete = null;
            for (PasswordRecoveryQuestionInfo passwordRecoveryQuestionToDelete : passwordRecoveryQuestionsToDelete) {
                securityQuestionToDelete = passwordRecoveryQuestionToDelete.getSecurityQuestion();
                if (securityQuestionToDelete != null) {
                    securityQuestionsToDelete.add(securityQuestionToDelete);
                }
            }
            addSecurityQuestions(securityQuestionsForUpdate);
            removeSecurityQuestions(securityQuestionsToDelete);
            PasswordRecoveryQuestionInfo passwordRecoveryQuestionInfo = null;
            for (SecurityQuestion updatedSecurityQuestion : securityQuestionsForUpdate) {
                passwordRecoveryQuestionInfo = currentSecurityQuestionsMap.get(updatedSecurityQuestion.getPurposeAndQuestion());
                passwordRecoveryQuestionInfo.setSecurityQuestion(updatedSecurityQuestion);
                passwordRecoveryQuestionInfo.setStatus(PasswordRecoveryQuestionInfo.UPTODATE);
            }
        } catch (Exception ex) {
            for (PasswordRecoveryQuestionInfo passwordRecoveryQuestionInfo : passwordRecoveryQuestionInfosForUpdate) {
                passwordRecoveryQuestionInfo.setStatus(PasswordRecoveryQuestionInfo.ERROR);
                passwordRecoveryQuestionInfo.setInfo(ex.getMessage());
            }
        }
    }

}
