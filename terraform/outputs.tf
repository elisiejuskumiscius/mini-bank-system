output "app_public_ip" {
  value = aws_instance.app.public_ip
}

output "db_private_ip" {
  value = aws_instance.db.private_ip
}
