package org.notificationengine.web.controller;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.notificationengine.persistance.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

@Controller
public class FileController {

    private static Logger LOGGER = Logger.getLogger(FileController.class);

    @Autowired
    private Persister persister;

    @RequestMapping(value = "/files/{file_id}.do", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getFile(@PathVariable("file_id") String fileId, HttpServletResponse response) {

        ObjectId fileObjectId = new ObjectId(fileId);

        File file = this.persister.retrieveFileFromId(fileObjectId);

        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

        String mimeType = mimeTypesMap.getContentType(file);

        LOGGER.debug("mimeType: " + mimeType);

        response.setContentType(mimeType);

        return new FileSystemResource(file);
    }

    @RequestMapping(value = "/files/{objectId}/{filename}.{ext}", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getFileFromFileName(
            @PathVariable("objectId") String objectIdString, @PathVariable("filename") String fileName, @PathVariable("ext") String ext) {

        ObjectId objectId = new ObjectId(objectIdString);

        String realFileName = fileName + "." + ext;

        File file = this.persister.retrieveFileFromIdAndFileName(objectId, realFileName);

        return new FileSystemResource(file);
    }

}
