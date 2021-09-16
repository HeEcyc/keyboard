#ifndef FLORISBOARD_LOG_H
#define FLORISBOARD_LOG_H

#include <string>

namespace utils {

void log_debug(const std::string& tag, const std::string& msg);
void log_info(const std::string& tag, const std::string& msg);
void log_warning(const std::string& tag, const std::string& msg);
void log_error(const std::string& tag, const std::string& msg);
void log_wtf(const std::string& tag, const std::string& msg);

int start_stdout_stderr_logger(const char *app_name);

} // namespace utils

#endif // FLORISBOARD_LOG_H
