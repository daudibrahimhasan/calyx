#!/bin/bash

# Setup script for CI/CD builds
# This ensures google-services.json exists (using dummy if needed)

echo "🔧 Setting up Firebase configuration for build..."

if [ -f "app/google-services.json" ]; then
    echo "✅ Found existing google-services.json"
else
    echo "⚠️  No google-services.json found"
    
    if [ -f "app/google-services.dummy.json" ]; then
        echo "📋 Copying dummy Firebase config..."
        cp app/google-services.dummy.json app/google-services.json
        echo "✅ Using dummy config for offline-only build"
    else
        echo "❌ ERROR: Neither real nor dummy google-services.json found!"
        exit 1
    fi
fi

echo "✅ Firebase configuration ready for build"
