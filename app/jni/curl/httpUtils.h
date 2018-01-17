#ifndef __HTTP_UTILS
#include "cJSON.h"
typedef enum  {
    SUCCESS = 0,
    FAIL,
} Status;
typedef void (*http_callback)(int,Status ,cJSON *);
void init_httpclient(const char *);
void get(const char*, http_callback);
void post(const char*,cJSON *,http_callback);
void cleanup();
#endif
