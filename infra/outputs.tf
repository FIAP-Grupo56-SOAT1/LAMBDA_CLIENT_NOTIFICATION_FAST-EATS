output "lambdas" {
  value = [{
    arn           = aws_lambda_function.lambda_notification.arn
    name          = aws_lambda_function.lambda_notification.function_name
    description   = aws_lambda_function.lambda_notification.description
    version       = aws_lambda_function.lambda_notification.version
    last_modified = aws_lambda_function.lambda_notification.last_modified
  }]
}
