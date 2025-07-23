# ----- Stage 1: Build the Angular application -----
# Use a specific Node version for reproducibility. 'alpine' is for a smaller image.
FROM node:20-alpine AS builder

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json to leverage Docker cache
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application source code
COPY . .

# Build the application for production
# The output will be in the /app/dist/skill-assessment-ui/browser directory
RUN npm run build -- --configuration production


# ----- Stage 2: Serve the application with Nginx -----
# Use a lightweight and stable Nginx image
FROM nginx:stable-alpine

# Remove the default Nginx welcome page
RUN rm /etc/nginx/conf.d/default.conf

# Copy our custom Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy the build artifacts from the 'builder' stage
# The source path is the output of the 'ng build' command
COPY --from=builder /app/dist/skill-assessment-ui/browser /usr/share/nginx/html

# Expose port 80 (the default Nginx port)
EXPOSE 80
