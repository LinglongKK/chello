#include "httpUtils.h"
#include <stdbool.h>
#include <malloc.h>
#include <stdlib.h>
#include "include/curl.h"
#include "thpool.h"
#include "../common_macro.h"
//global var
static CURL *httpclient;
static char *base;
static threadpool global_pool;

typedef struct {
    char *ptr;
    size_t len;
} string;

typedef struct {
    char *url;
    cJSON *post_data;
    string *response_data;
    http_callback callback;
} http_request;


static bool check();
static void merge_url(char **merge_url,const char *relative_url);
static void http_work(http_request *);
static void init_string(string *);
static size_t base_http_response(void *,size_t,size_t,http_request *);
static void clean_request(http_request **);
static void safe_free(void **);

void init_httpclient(const char *base_url) {
    curl_global_init(CURL_GLOBAL_ALL);
    httpclient = curl_easy_init();
    global_pool = thpool_init(3);
    base = malloc(strlen(base_url)+1);
    strcpy(base,base_url);
    curl_easy_setopt(httpclient, CURLOPT_FOLLOWLOCATION, 1L);
}

void get(const char *url, http_callback callback) {
    if(!check()) return ;
    char *full_url;
    merge_url(&full_url,url);
    http_request *request = malloc(sizeof(http_request));
    memset(request,0, sizeof(http_request));
    request->url = full_url;
    request->callback = callback;
    request->response_data = malloc(sizeof(string));
    init_string(request->response_data);
    thpool_add_work(global_pool,(void*)http_work,request);
}

void post(const char *url, cJSON *post_data, http_callback callback) {
    if(!check()) return;
}

void cleanup(){
    curl_easy_cleanup(httpclient);
    curl_global_cleanup();
    thpool_destroy(global_pool);
    httpclient = NULL;
    global_pool = NULL;
    safe_free((void *)&base);
}


static void merge_url(char **merge_url, const char *relative_url) {
    *merge_url= malloc(sizeof(char) * (strlen(relative_url)+strlen(base) + 1));
    strcpy(*merge_url,base);
    strcat(*merge_url,relative_url);
}

void init_string(string *s) {
    s->len = 0;
    s->ptr = malloc(s->len+1);
    if (s->ptr == NULL) {
        fprintf(stderr, "malloc() failed\n");
    }
    s->ptr[s->len] = '\0';
}

static void http_work(http_request *request) {
    curl_easy_setopt(httpclient, CURLOPT_URL, request->url);
    curl_easy_setopt(httpclient, CURLOPT_WRITEFUNCTION, base_http_response);
    curl_easy_setopt(httpclient, CURLOPT_WRITEDATA,request);
    CURLcode res = curl_easy_perform(httpclient);
    if(res == CURLE_OK) {
#ifdef __DEBUG
        LOG_INFO(request->response_data->ptr);
#endif
        long http_status_code;
        curl_easy_getinfo(httpclient,CURLINFO_RESPONSE_CODE,&http_status_code);
        cJSON *parsed = cJSON_Parse(request->response_data->ptr);
        if(request->callback != NULL) {
            request->callback(http_status_code,SUCCESS,parsed);
        }
        cJSON_Delete(parsed);
    } else {
        request->callback(res,FAIL,NULL);
        LOG("curl res code :%d",res);
    }
    clean_request(&request);
}


static bool check() {
    if(httpclient == NULL) return false;
    if(global_pool == NULL) return false;
    return true;
}

size_t base_http_response(void *ptr, size_t size ,size_t nmemb, http_request * request) {
    if(request == NULL) return (size_t)-1;
    size_t new_len = request->response_data->len + size * nmemb;
    request->response_data->ptr = realloc(request->response_data->ptr,new_len+1);
    if(request->response_data->ptr == NULL) {
        LOG_INFO("realloc() failed");
        exit(EXIT_FAILURE);
    }

    memcpy(request->response_data->ptr,ptr,size * nmemb);
    request->response_data->ptr[new_len] = '\0';
    request->response_data->len = new_len;
    return size * nmemb;
}

static void clean_request(http_request **request) {
    http_request *req = *request;
    safe_free((void*)&(req->url));
    safe_free((void*)&(req->response_data->ptr));
    safe_free((void*)&(req->response_data));
    safe_free((void*)request);
}

static void safe_free(void **ptr) {
    if(ptr == NULL || *ptr == NULL) return ;
    free(*ptr);
    *ptr = NULL;
}
