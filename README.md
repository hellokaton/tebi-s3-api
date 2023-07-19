# tebi.io Object Storage API

This is a simple project to use the Amazon S3 API with tebi Object Storage.

--- 

Env file example:
```
access_key=your_access_key
secret_key=your_secret_key
bucket=your_bucket_name
endpoint_url=https://s3.tebi.io
region=your_region
```

Features:
- [X] Upload file
- [X] Download single file

--- 

Endpoints example:

Upload file:
```
POST tebi/upload

BODY (form-data):
    file: file_to_upload
```
---
Download file:
```
GET tebi/download?key={key_on_object_storage}
```
---
